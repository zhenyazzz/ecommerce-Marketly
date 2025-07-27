package org.com.orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.orderservice.dto.request.OrderRequest;
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

    @Operation(summary = "Create new order")
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(name = "X-User-Id") @Parameter(description = "User ID") UUID userId,
            @Valid @RequestBody OrderRequest request) {

        log.info("Creating order for user: {}", userId);
        OrderResponse response = orderService.createOrder(userId, request);

        return ResponseEntity
                .accepted()
                .header("Location", "/api/v1/orders/" + response.orderId())
                .body(response);
    }

    @Operation(summary = "Get order details")
    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @PathVariable @Parameter(description = "Order ID") UUID orderId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.debug("Fetching order {} for user {}", orderId, userId);
        return orderService.getOrder(orderId, userId);
    }

    @Operation(summary = "List user orders")
    @GetMapping
    public List<OrderResponse> getUserOrders(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(required = false) OrderStatus status) {

        log.debug("Fetching orders for user {} with status {}", userId, status);
        return orderService.getUserOrders(userId);
    }

    @Operation(summary = "Update order status")
    @PatchMapping("/{orderId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam @Valid OrderStatus status
            ) {

        log.info("Updating status for order {} to {}", orderId, status);
        orderService.updateOrderStatus(orderId, status);
    }

    @Operation(summary = "Cancel order")
    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(
            @PathVariable UUID orderId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.warn("User {} is cancelling order {}", userId, orderId);
        orderService.cancelOrder(orderId, userId);
    }

}
