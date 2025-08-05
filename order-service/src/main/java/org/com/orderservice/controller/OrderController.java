package org.com.orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.orderservice.dto.request.CreateOrderRequest;
import org.com.orderservice.dto.request.UpdateOrderRequest;
import org.com.orderservice.dto.response.OrderResponse;
import org.com.orderservice.model.OrderStatus;
import org.com.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "List all orders")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(
    ){
        try {
            log.debug("Fetching all orders");
            List<OrderResponse> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching all orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @Operation(summary = "Create new order")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(name = "X-User-Id") @Parameter(description = "User ID") Long userId,
            @Valid @RequestBody CreateOrderRequest request
    ){
        try {
            log.info("Creating order for user: {}", userId);
            OrderResponse response = orderService.createOrder(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating order for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @Operation(summary = "Get order details")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable @Parameter(description = "Order ID") UUID orderId,
            @RequestHeader("X-User-Id") Long userId
    ){
        try {
            log.debug("Fetching order {} for user {}", orderId, userId);
            OrderResponse response = orderService.getOrder(orderId, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching order {} for user {}", orderId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @Operation(summary = "List user orders by status")
    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getUserOrdersByStatus(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) OrderStatus status
    ){
        try {
            log.debug("Fetching orders for user {} with status {}", userId, status);
            List<OrderResponse> orders = orderService.getUserOrdersByStatus(userId, status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching orders for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @Operation(summary = "List all user orders")
    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getAllUserOrders(
            @RequestHeader("X-User-Id") Long userId
    ){
        try {
            log.debug("Fetching all orders for user {}", userId);
            List<OrderResponse> orders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching orders for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @Operation(summary = "Update order status")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam @Valid OrderStatus status
    ){
        try {
            log.info("Updating status for order {} to {}", orderId, status);
            orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error updating status for order {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @Operation(summary = "Update order")
    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderRequest request
    ){
        try {
            log.info("Updating order {} with data {}", orderId, request);
            OrderResponse response = orderService.updateOrder(orderId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating order {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @Operation(summary = "Cancel order")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId,
            @RequestHeader("X-User-Id") Long userId
    ){
        try {
            log.warn("User {} is cancelling order {}", userId, orderId);
            orderService.cancelOrder(orderId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error cancelling order {} by user {}", orderId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }










}