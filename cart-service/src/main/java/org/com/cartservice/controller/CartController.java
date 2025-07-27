package org.com.cartservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.cartservice.dto.request.AddItemRequest;
import org.com.cartservice.dto.response.CartResponse;
import org.com.cartservice.exception.ErrorResponse;
import org.com.cartservice.exception.ResourceNotFoundException;
import org.com.cartservice.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addItemToCart(
            @RequestBody @Valid AddItemRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        return cartService.addItem(userId, request);
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItemFromCart(
            @PathVariable UUID productId,
            @RequestHeader("X-User-Id") UUID userId) {
        cartService.removeItem(userId, productId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(
            @RequestHeader("X-User-Id") UUID userId) {
        cartService.clearCart(userId);
    }

    @GetMapping
    public CartResponse getCart(
            @RequestHeader("X-User-Id") UUID userId) {
        return cartService.getCart(userId);
    }
    @DeleteMapping
    public void deleteCart(@RequestHeader("X-User-Id") UUID userId){
        cartService.clearCart(userId);
    }

}
