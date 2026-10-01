package com.spring_microservice.order_service.controller;

import com.spring_microservice.order_service.dto.OrderRequest;
import com.spring_microservice.order_service.dto.OrderResponse;
import com.spring_microservice.order_service.service.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================
    // CREATE
    // =========================

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest request) {

        OrderResponse createdOrder = orderService.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdOrder);
    }

    // =========================
    // TEST API GATEWAY
    // =========================
    @GetMapping("/test")
    public String test() {
        return "Order is working";
    }

    // =========================
    // READ ALL
    // =========================

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(
                orderService.getAll());
    }

    // =========================
    // READ ONE
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id));
    }

    // =========================
    // UPDATE
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @RequestBody OrderRequest request) {

        return ResponseEntity.ok(
                orderService.updateOrder(id, request));
    }

    // =========================
    // DELETE
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}