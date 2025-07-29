package org.com.cartservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.cartservice.dto.request.AddItemRequest;
import org.com.cartservice.dto.response.CartResponse;
import org.com.cartservice.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CartResponse> addItemToCart(
            @RequestBody @Valid AddItemRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addItem(userId, request));
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItemFromCart(
            @PathVariable UUID productId,
            @RequestHeader("X-User-Id") Long userId) {
        cartService.removeItem(userId, productId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(
            @RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<CartResponse>> getCartHistory(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getCartHistory(userId));
    }

    @PostMapping("/restore")
    public ResponseEntity<CartResponse> restoreLastCart(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.restoreLastCart(userId));
    }

    // Внутренний endpoint для Order Service
    @PostMapping("/convert-to-order")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void convertCartToOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long orderId) {
        cartService.convertCartToOrder(userId, orderId);
    }
}
