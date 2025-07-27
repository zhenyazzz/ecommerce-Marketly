package org.com.cartservice.service;

import lombok.RequiredArgsConstructor;
import org.com.cartservice.controller.ProductServiceClient;
import org.com.cartservice.dto.mapper.CartMapper;
import org.com.cartservice.dto.request.AddItemRequest;
import org.com.cartservice.dto.response.CartResponse;
import org.com.cartservice.dto.response.ProductDto;
import org.com.cartservice.exception.ResourceNotFoundException;
import org.com.cartservice.model.Cart;
import org.com.cartservice.model.CartItem;
import org.com.cartservice.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductServiceClient productServiceClient;

    // Добавить товар в корзину
    public CartResponse addItem(UUID userId, AddItemRequest request) {
        // 1. Получаем или создаем корзину
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        // 2. Проверяем товар в ProductService
        ProductDto product = productServiceClient.getProduct(request.productId());
        if (product.stock() < request.quantity()) {
            throw new IllegalStateException("Not enough stock");
        }

        // 3. Обновляем корзину
        CartItem newItem = CartItem.builder()
                .productId(product.id())
                .name(product.name())
                .price(product.price())
                .quantity(request.quantity())
                .build();

        cart.getCartItems().add(newItem);
        cart.setTotal(calculateTotal(cart.getCartItems()));

        // 4. Сохраняем и возвращаем DTO
        Cart savedCart = cartRepository.save(cart);
        return CartMapper.toCartResponse(savedCart);
    }

    // Удалить товар из корзины
    public void removeItem(UUID userId, UUID productId) {
        Cart cart = getCartOrThrow(userId);
        boolean removed = cart.getCartItems().removeIf(item -> item.getProductId().equals(productId));

        if (removed) {
            cart.setTotal(calculateTotal(cart.getCartItems()));
            cartRepository.save(cart);
        }
    }

    // Очистить корзину
    public void clearCart(UUID userId) {
        Cart cart = getCartOrThrow(userId);
        cart.getCartItems().clear();
        cart.setTotal(BigDecimal.valueOf(0.0));
        cartRepository.save(cart);
    }

    // Получить корзину
    public CartResponse getCart(UUID userId) {
        Cart cart = getCartOrThrow(userId);
        return CartMapper.toCartResponse(cart);
    }

    // --- Вспомогательные методы ---
    private Cart createNewCart(UUID userId) {
        return Cart.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .cartItems(new ArrayList<>())
                .total(BigDecimal.valueOf(0.0))
                .build();
    }

    private Cart getCartOrThrow(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + userId));
    }

    private BigDecimal calculateTotal(List<CartItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
