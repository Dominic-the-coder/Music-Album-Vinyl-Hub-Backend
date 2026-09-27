package com.example.mini_project.controllers;

import com.example.mini_project.models.Order;
import com.example.mini_project.models.OrderStatus;
import com.example.mini_project.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService service;

    // GET /orders
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return service.getAllOrders();
    }

    // GET /orders/{id}
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable int id) {

        Order order = service.getOrderById(id);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(order);
    }

    // GET /orders/user/{userId}
    @GetMapping("/orders/user/{userId}")
    public List<Order> getOrdersByUserId(
            @PathVariable int userId) {

        return service.getOrdersByUserId(userId);
    }

    // POST /orders
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(
            @RequestBody Order order) {

        Order createdOrder = service.createOrder(order);

        return ResponseEntity.ok(createdOrder);
    }

    // PUT /orders/{id}/status
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable int id,
            @RequestParam OrderStatus status) {

        Order order = service.updateOrderStatus(id, status);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(order);
    }

    // DELETE /orders/{id}
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable int id) {

        boolean deleted = service.deleteOrder(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}