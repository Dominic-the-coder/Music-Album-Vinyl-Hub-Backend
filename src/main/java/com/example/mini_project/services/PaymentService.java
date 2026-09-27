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
    // CREATE CHECKOUT SESSION
    // ============================================================

    public String createCheckoutSession(
            int cartId
    ) throws StripeException {

        Stripe.apiKey = stripeSecretKey;

        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElse(null);

        if (cart == null) {
            throw new RuntimeException(
                    "Cart not found"
            );
        }

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(
                        cartId
                );

        if (cartItems == null ||
                cartItems.isEmpty()) {

            throw new RuntimeException(
                    "Cart is empty"
            );
        }


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


        for (CartItem cartItem : cartItems) {

            if (cartItem.getQuantity() < 1) {

                throw new RuntimeException(
                        "Invalid cart quantity"
                );
            }


            Album album =
                    albumRepository
                            .findById(
                                    cartItem.getAlbumId()
                            )
                            .orElse(null);

            if (album == null) {

                throw new RuntimeException(
                        "Album not found"
                );
            }


            BigDecimal itemPrice =
                    BigDecimal.valueOf(
                            cartItem.getPrice()
                    );


            if (itemPrice.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new RuntimeException(
                        "Invalid album price"
                );
            }


            long priceInSen =
                    itemPrice
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .longValue();


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
        }


        Session session =
                Session.create(
                        sessionBuilder.build()
                );


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

    @Transactional
    public String getPaymentStatus(
            String stripeSessionId
    ) throws StripeException {

        Stripe.apiKey = stripeSecretKey;


        Payment payment =
                paymentRepository
                        .findByStripeSessionId(
                                stripeSessionId
                        );


        if (payment == null) {

            throw new RuntimeException(
                    "Payment not found"
            );
        }


        // Already processed
        if ("paid".equalsIgnoreCase(
                payment.getPaymentStatus()
        )) {

            return "paid";
        }


        Session session =
                Session.retrieve(
                        stripeSessionId
                );


        String stripeStatus =
                session.getPaymentStatus();


        if ("paid".equalsIgnoreCase(
                stripeStatus
        )) {

            markPaymentAsPaid(
                    stripeSessionId
            );

            return "paid";
        }


        return stripeStatus;
    }


    // ============================================================
    // PROCESS SUCCESSFUL PAYMENT
    // ============================================================

    @Transactional
    public int markPaymentAsPaid(
            String stripeSessionId
    ) {

        Payment payment =
                paymentRepository
                        .findByStripeSessionId(
                                stripeSessionId
                        );


        if (payment == null) {

            throw new RuntimeException(
                    "Payment not found"
            );
        }


        // --------------------------------------------------------
        // PREVENT DUPLICATE ORDER
        // --------------------------------------------------------

        if ("paid".equalsIgnoreCase(
                payment.getPaymentStatus()
        )) {

            if (payment.getOrderId() != null) {
                return payment.getOrderId();
            }

            throw new RuntimeException(
                    "Payment already processed"
            );
        }


        // --------------------------------------------------------
        // GET CART
        // --------------------------------------------------------

        Cart cart =
                cartRepository
                        .findById(
                                payment.getCartId()
                        )
                        .orElse(null);


        if (cart == null) {

            throw new RuntimeException(
                    "Cart not found"
            );
        }


        // --------------------------------------------------------
        // GET CART ITEMS
        // --------------------------------------------------------

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(
                        cart.getId()
                );


        if (cartItems == null ||
                cartItems.isEmpty()) {

            throw new RuntimeException(
                    "Cart is empty"
            );
        }


        // --------------------------------------------------------
        // CREATE ORDER
        // --------------------------------------------------------

        Order order =
                new Order();


        order.setUserId(
                cart.getUserId()
        );


        order.setStatus(
                OrderStatus.PAID
        );


        order.setCreatedAt(
                java.time.LocalDateTime.now()
        );


        List<OrderItem> orderItems =
                new ArrayList<>();


        BigDecimal totalAmount =
                BigDecimal.ZERO;


        // --------------------------------------------------------
        // COPY CART ITEMS
        // --------------------------------------------------------

        for (CartItem cartItem :
                cartItems) {

            if (cartItem.getQuantity() < 1) {

                throw new RuntimeException(
                        "Invalid cart quantity"
                );
            }


            Album album =
                    albumRepository
                            .findById(
                                    cartItem.getAlbumId()
                            )
                            .orElse(null);


            if (album == null) {

                throw new RuntimeException(
                        "Album not found"
                );
            }


            BigDecimal price =
                    BigDecimal.valueOf(
                            cartItem.getPrice()
                    );


            OrderItem orderItem =
                    new OrderItem();


            orderItem.setAlbumId(
                    album.getId()
            );


            orderItem.setAlbumTitle(
                    album.getTitle()
            );


            orderItem.setPrice(
                    price
            );


            orderItem.setQuantity(
                    cartItem.getQuantity()
            );


            // IMPORTANT
            orderItem.setOrder(
                    order
            );


            orderItems.add(
                    orderItem
            );


            BigDecimal itemTotal =
                    price.multiply(
                            BigDecimal.valueOf(
                                    cartItem.getQuantity()
                            )
                    );


            totalAmount =
                    totalAmount.add(
                            itemTotal
                    );
        }


        // --------------------------------------------------------
        // ATTACH ITEMS TO ORDER
        // --------------------------------------------------------

        order.setItems(
                orderItems
        );


        order.setTotalAmount(
                totalAmount
        );


        // --------------------------------------------------------
        // SAVE ORDER
        // --------------------------------------------------------

        Order savedOrder =
                orderService.createOrder(
                        order
                );


        if (savedOrder == null ||
                savedOrder.getId() <= 0) {

            throw new RuntimeException(
                    "Unable to create order"
            );
        }


        // --------------------------------------------------------
        // MARK PAYMENT AS PAID
        // --------------------------------------------------------

        payment.setPaymentStatus(
                "paid"
        );


        payment.setOrderId(
                savedOrder.getId()
        );


        paymentRepository.save(
                payment
        );


        // --------------------------------------------------------
        // DELETE CART ITEMS
        // --------------------------------------------------------

        cartItemRepository.deleteAll(
                cartItems
        );


        // Force delete to database
        cartItemRepository.flush();


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