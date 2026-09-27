package com.example.mini_project.services;

import com.example.mini_project.models.Order;
import com.example.mini_project.models.OrderItem;
import com.example.mini_project.models.OrderStatus;
import com.example.mini_project.repositories.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    public List<Order> getAllOrders() {

        return repository.findAll();
    }

    public Order getOrderById(int id) {

        return repository
                .findById(id)
                .orElse(null);
    }

    public List<Order> getOrdersByUserId(
            int userId
    ) {

        return repository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order createOrder(
            Order order
    ) {

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

        if (order.getItems() != null) {

            for (OrderItem item :
                    order.getItems()) {

                item.setOrder(order);
            }
        }

        return repository.save(order);
    }

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

    public boolean deleteOrder(int id) {

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);

        return true;
    }
}