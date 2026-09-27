package com.example.mini_project.services;

import com.example.mini_project.models.Album;
import com.example.mini_project.models.Cart;
import com.example.mini_project.models.CartItem;
import com.example.mini_project.models.Order;
import com.example.mini_project.models.OrderItem;
import com.example.mini_project.models.OrderStatus;
import com.example.mini_project.models.Payment;

import com.example.mini_project.repositories.AlbumRepository;
import com.example.mini_project.repositories.CartItemRepository;
import com.example.mini_project.repositories.CartRepository;
import com.example.mini_project.repositories.PaymentRepository;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private OrderService orderService;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;


    // ============================================================
    // CREATE STRIPE CHECKOUT SESSION
    // ============================================================

    public String createCheckoutSession(int cartId)
            throws StripeException {

        Stripe.apiKey = stripeSecretKey;


        // ========================================================
        // GET CART
        // ========================================================

        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElse(null);

        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }


        // ========================================================
        // GET CART ITEMS
        // ========================================================

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(cartId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }


        // ========================================================
        // CREATE STRIPE SESSION
        // ========================================================

        SessionCreateParams.Builder sessionBuilder =
                SessionCreateParams
                        .builder()
                        .setMode(
                                SessionCreateParams.Mode.PAYMENT
                        )
                        .setSuccessUrl(
                                "miniproject://payment-success?session_id={CHECKOUT_SESSION_ID}"
                        )
                        .setCancelUrl(
                                "miniproject://payment-cancel"
                        )
                        .putMetadata(
                                "cartId",
                                String.valueOf(cartId)
                        );


        // ========================================================
        // ADD CART ITEMS TO STRIPE
        // ========================================================

        for (CartItem cartItem : cartItems) {

            // ----------------------------------------------------
            // Validate quantity
            // ----------------------------------------------------

            if (cartItem.getQuantity() < 1) {

                throw new RuntimeException(
                        "Invalid quantity for cart item: "
                                + cartItem.getId()
                );
            }


            // ----------------------------------------------------
            // Get album
            // ----------------------------------------------------

            Album album =
                    albumRepository
                            .findById(
                                    cartItem.getAlbumId()
                            )
                            .orElse(null);

            if (album == null) {

                throw new RuntimeException(
                        "Album not found: "
                                + cartItem.getAlbumId()
                );
            }


            // ----------------------------------------------------
            // Get price from CartItem
            // ----------------------------------------------------

            BigDecimal itemPrice =
                    BigDecimal.valueOf(
                            cartItem.getPrice()
                    );


            if (itemPrice.compareTo(BigDecimal.ZERO) <= 0) {

                throw new RuntimeException(
                        "Invalid price for album: "
                                + album.getTitle()
                );
            }


            // ----------------------------------------------------
            // Convert RM to sen
            // ----------------------------------------------------

            long priceInSen =
                    itemPrice
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .longValue();


            // ----------------------------------------------------
            // Product information
            // ----------------------------------------------------

            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams
                            .LineItem
                            .PriceData
                            .ProductData
                            .builder()
                            .setName(
                                    album.getTitle()
                            )
                            .build();


            // ----------------------------------------------------
            // Stripe price
            // ----------------------------------------------------

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams
                            .LineItem
                            .PriceData
                            .builder()
                            .setCurrency("myr")
                            .setUnitAmount(
                                    priceInSen
                            )
                            .setProductData(
                                    productData
                            )
                            .build();


            // ----------------------------------------------------
            // Stripe line item
            // ----------------------------------------------------

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams
                            .LineItem
                            .builder()
                            .setQuantity(
                                    (long) cartItem.getQuantity()
                            )
                            .setPriceData(
                                    priceData
                            )
                            .build();


            sessionBuilder.addLineItem(
                    lineItem
            );


            // ----------------------------------------------------
            // Debug information
            // ----------------------------------------------------

            System.out.println(
                    "Stripe item: "
                            + album.getTitle()
                            + " | Price: RM "
                            + cartItem.getPrice()
                            + " | Quantity: "
                            + cartItem.getQuantity()
            );
        }


        // ========================================================
        // CREATE STRIPE SESSION
        // ========================================================

        Session session =
                Session.create(
                        sessionBuilder.build()
                );


        // ========================================================
        // SAVE PAYMENT
        // ========================================================

        Payment payment =
                new Payment();

        payment.setCartId(
                cartId
        );

        payment.setPaymentType(
                "stripe"
        );

        payment.setPaymentStatus(
                "pending"
        );

        payment.setStripeSessionId(
                session.getId()
        );

        paymentRepository.save(
                payment
        );


        return session.getUrl();
    }


    // ============================================================
    // CHECK PAYMENT STATUS
    // ============================================================

    public String getPaymentStatus(
            String stripeSessionId
    ) throws StripeException {

        Stripe.apiKey = stripeSecretKey;


        // ========================================================
        // FIND PAYMENT
        // ========================================================

        Payment payment =
                paymentRepository
                        .findByStripeSessionId(
                                stripeSessionId
                        );

        if (payment == null) {

            throw new RuntimeException(
                    "Payment not found for Stripe session: "
                            + stripeSessionId
            );
        }


        // ========================================================
        // ALREADY PROCESSED
        // ========================================================

        if ("paid".equalsIgnoreCase(
                payment.getPaymentStatus()
        )) {

            return "paid";
        }


        // ========================================================
        // GET STRIPE SESSION
        // ========================================================

        Session session =
                Session.retrieve(
                        stripeSessionId
                );


        String stripePaymentStatus =
                session.getPaymentStatus();


        // ========================================================
        // PAYMENT SUCCESSFUL
        // ========================================================

        if ("paid".equalsIgnoreCase(
                stripePaymentStatus
        )) {

            markPaymentAsPaid(
                    stripeSessionId
            );

            return "paid";
        }


        return stripePaymentStatus;
    }


    // ============================================================
    // MARK PAYMENT AS PAID
    // CREATE ORDER
    // CLEAR CART
    // ============================================================

    @Transactional
    public int markPaymentAsPaid(
            String stripeSessionId
    ) {

        // ========================================================
        // FIND PAYMENT
        // ========================================================

        Payment payment =
                paymentRepository
                        .findByStripeSessionId(
                                stripeSessionId
                        );

        if (payment == null) {

            throw new RuntimeException(
                    "Payment not found for Stripe session: "
                            + stripeSessionId
            );
        }


        // ========================================================
        // PREVENT DUPLICATE ORDER
        // ========================================================

        if ("paid".equalsIgnoreCase(
                payment.getPaymentStatus()
        )) {

            if (payment.getOrderId() != null) {
                return payment.getOrderId();
            }

            throw new RuntimeException(
                    "Payment is already marked as paid but has no order"
            );
        }


        // ========================================================
        // GET CART
        // ========================================================

        Cart cart =
                cartRepository
                        .findById(
                                payment.getCartId()
                        )
                        .orElse(null);

        if (cart == null) {

            throw new RuntimeException(
                    "Cart not found: "
                            + payment.getCartId()
            );
        }


        // ========================================================
        // GET CART ITEMS
        // ========================================================

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(
                        cart.getId()
                );

        if (cartItems == null || cartItems.isEmpty()) {

            throw new RuntimeException(
                    "Cart has no items"
            );
        }


        // ========================================================
        // CREATE ORDER
        // ========================================================

        Order order =
                new Order();

        order.setUserId(
                cart.getUserId()
        );

        order.setStatus(
                OrderStatus.PAID
        );


        List<OrderItem> orderItems =
                new ArrayList<>();

        BigDecimal totalAmount =
                BigDecimal.ZERO;


        // ========================================================
        // CONVERT CART ITEMS TO ORDER ITEMS
        // ========================================================

        for (CartItem cartItem : cartItems) {

            // ----------------------------------------------------
            // Validate quantity
            // ----------------------------------------------------

            if (cartItem.getQuantity() < 1) {

                throw new RuntimeException(
                        "Invalid quantity for cart item: "
                                + cartItem.getId()
                );
            }


            // ----------------------------------------------------
            // Get album
            // ----------------------------------------------------

            Album album =
                    albumRepository
                            .findById(
                                    cartItem.getAlbumId()
                            )
                            .orElse(null);

            if (album == null) {

                throw new RuntimeException(
                        "Album not found: "
                                + cartItem.getAlbumId()
                );
            }


            // ----------------------------------------------------
            // Use CartItem price
            // ----------------------------------------------------

            BigDecimal itemPrice =
                    BigDecimal.valueOf(
                            cartItem.getPrice()
                    );


            // ----------------------------------------------------
            // Create OrderItem
            // ----------------------------------------------------

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setAlbumId(
                    album.getId()
            );

            orderItem.setAlbumTitle(
                    album.getTitle()
            );

            orderItem.setPrice(
                    itemPrice
            );

            orderItem.setQuantity(
                    cartItem.getQuantity()
            );

            orderItem.setOrder(
                    order
            );


            orderItems.add(
                    orderItem
            );


            // ----------------------------------------------------
            // Calculate item total
            // ----------------------------------------------------

            BigDecimal itemTotal =
                    itemPrice.multiply(
                            BigDecimal.valueOf(
                                    cartItem.getQuantity()
                            )
                    );


            totalAmount =
                    totalAmount.add(
                            itemTotal
                    );


            // ----------------------------------------------------
            // Debug information
            // ----------------------------------------------------

            System.out.println(
                    "Order item: "
                            + album.getTitle()
                            + " | Price: RM "
                            + cartItem.getPrice()
                            + " | Quantity: "
                            + cartItem.getQuantity()
                            + " | Total: RM "
                            + itemTotal
            );
        }


        // ========================================================
        // SET ORDER DATA
        // ========================================================

        order.setItems(
                orderItems
        );

        order.setTotalAmount(
                totalAmount
        );


        // ========================================================
        // SAVE ORDER
        // ========================================================

        Order savedOrder =
                orderService.createOrder(
                        order
                );


        if (savedOrder == null) {

            throw new RuntimeException(
                    "Failed to create order"
            );
        }


        // ========================================================
        // MARK PAYMENT AS PAID
        // ========================================================

        payment.setPaymentStatus(
                "paid"
        );

        payment.setOrderId(
                savedOrder.getId()
        );

        paymentRepository.save(
                payment
        );


        // ========================================================
        // CLEAR CART
        // ========================================================

        cartItemRepository.deleteAll(
                cartItems
        );


        System.out.println(
                "Payment completed. "
                        + "Order ID: "
                        + savedOrder.getId()
                        + " | Cart ID: "
                        + cart.getId()
        );


        return savedOrder.getId();
    }


    // ============================================================
    // GET ORDER ID
    // ============================================================

    public Integer getOrderId(
            String stripeSessionId
    ) {

        Payment payment =
                paymentRepository
                        .findByStripeSessionId(
                                stripeSessionId
                        );

        if (payment == null) {
            return null;
        }

        return payment.getOrderId();
    }
}