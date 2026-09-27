package com.example.mini_project.services;

import com.example.mini_project.models.Order;
import com.example.mini_project.models.OrderItem;
import com.example.mini_project.models.OrderStatus;
import com.example.mini_project.repositories.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;


    // ============================================================
    // GET ALL ORDERS
    // ============================================================

    public List<Order> getAllOrders() {

        return repository.findAll();
    }


    // ============================================================
    // GET ORDER BY ID
    // ============================================================

    public Order getOrderById(
            int id
    ) {

        return repository
                .findById(id)
                .orElse(null);
    }


    // ============================================================
    // GET ORDERS BY USER
    // ============================================================

    public List<Order> getOrdersByUserId(
            int userId
    ) {

        return repository
                .findByUserIdOrderByCreatedAtDesc(
                        userId
                );
    }


    // ============================================================
    // CREATE ORDER
    // ============================================================

    @Transactional
    public Order createOrder(
            Order order
    ) {

        if (order == null) {
            throw new RuntimeException(
                    "Order cannot be null"
            );
        }


        if (order.getCreatedAt() == null) {

            order.setCreatedAt(
                    LocalDateTime.now()
            );
        }


        if (order.getStatus() == null) {

            order.setStatus(
                    OrderStatus.PENDING
            );
        }


        if (order.getItems() == null ||
                order.getItems().isEmpty()) {

            throw new RuntimeException(
                    "Order must contain at least one item"
            );
        }


        for (OrderItem item :
                order.getItems()) {

            if (item == null) {
                continue;
            }

            item.setOrder(order);
        }


        return repository.save(order);
    }


    // ============================================================
    // UPDATE ORDER STATUS
    // ============================================================

    public Order updateOrderStatus(
            int orderId,
            OrderStatus status
    ) {

        Order order =
                repository
                        .findById(orderId)
                        .orElse(null);

        if (order == null) {
            return null;
        }


        order.setStatus(status);

        return repository.save(order);
    }


    // ============================================================
    // DELETE ORDER
    // ============================================================

    public boolean deleteOrder(
            int id
    ) {

        if (!repository.existsById(id)) {
            return false;
        }


        repository.deleteById(id);

        return true;
    }
}