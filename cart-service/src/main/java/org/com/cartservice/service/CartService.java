package org.com.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.cartservice.controller.ProductServiceClient;

import org.com.cartservice.dto.mapper.CartMapper;
import org.com.cartservice.dto.request.AddItemRequest;
import org.com.cartservice.dto.response.CartResponse;
import org.com.cartservice.dto.response.ProductDto;
import org.com.cartservice.exception.ResourceNotFoundException;
import org.com.cartservice.model.Cart;
import org.com.cartservice.model.CartItem;
import org.com.cartservice.model.CartStatus;
import org.com.cartservice.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductServiceClient productServiceClient;
    private final CartCacheService cartCacheService;

    // Добавить товар в корзину
    public CartResponse addItem(Long userId, AddItemRequest request) {
        // 1. Получаем активную корзину или создаем новую
        Cart cart = getOrCreateActiveCart(userId);

        // 2. Проверяем товар в ProductService
        ProductDto product = productServiceClient.getProduct(request.productId());
        if (product.quantity() < request.quantity()) {
            throw new IllegalStateException("Not enough stock");
        }

        // 3. Проверяем, есть ли уже такой товар в корзине
        boolean itemExists = cart.getCartItems().stream()
                .anyMatch(item -> item.getProductId().equals(product.id()));

        if (itemExists) {
            // Обновляем количество существующего товара
            cart.getCartItems().stream()
                    .filter(item -> item.getProductId().equals(product.id()))
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(item.getQuantity() + request.quantity()));
        } else {
            // Добавляем новый товар
            CartItem newItem = CartItem.builder()
                    .productId(product.id())
                    .name(product.name())
                    .price(product.price())
                    .quantity(request.quantity())
                    .build();
            cart.addItem(newItem);
        }

        // 4. Обновляем метаданные
        cart.updateActivity();

        // 5. Сохраняем в БД
        Cart savedCart = cartRepository.save(cart);
        
        // 6. Обновляем кеш
        CartResponse cartResponse = cartMapper.toCartResponse(savedCart);
        cartCacheService.updateCachedCart(userId, cartResponse);
        
        return cartResponse;
    }

    // Удалить товар из корзины
    public void removeItem(Long userId, UUID productId) {
        Cart cart = getActiveCartOrThrow(userId);
        cart.removeItem(productId);
        cart.updateActivity();
        
        // Сохраняем в БД
        cartRepository.save(cart);
        
        // Обновляем кеш
        CartResponse cartResponse = cartMapper.toCartResponse(cart);
        cartCacheService.updateCachedCart(userId, cartResponse);
    }

    // Очистить корзину
    public void clearCart(Long userId) {
        Cart cart = getActiveCartOrThrow(userId);
        cart.getCartItems().clear();
        cart.recalculateTotal();
        cart.updateActivity();
        
        // Сохраняем в БД
        cartRepository.save(cart);
        
        // Обновляем кеш
        CartResponse cartResponse = cartMapper.toCartResponse(cart);
        cartCacheService.updateCachedCart(userId, cartResponse);
    }

    // Получить активную корзину (с кешированием)
    public CartResponse getCart(Long userId) {
        // 1. Пробуем получить из кеша
        return cartCacheService.getCachedCart(userId)
                .orElseGet(() -> {
                    // 2. Если нет в кеше, получаем из БД
                    Cart cart = getActiveCartOrThrow(userId);
                    CartResponse cartResponse = cartMapper.toCartResponse(cart);
                    
                    // 3. Сохраняем в кеш
                    cartCacheService.cacheCart(userId, cartResponse);
                    
                    return cartResponse;
                });
    }

    // Получить историю корзин
    public List<CartResponse> getCartHistory(Long userId) {
        List<Cart> carts = cartRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return carts.stream()
                .map(cartMapper::toCartResponse)
                .toList();
    }

    // Преобразовать корзину в заказ (вызывается из Order Service)
    public void convertCartToOrder(Long userId, Long orderId) {
        Cart cart = getActiveCartOrThrow(userId);
        
        // Архивируем текущую корзину
        cart.archive(orderId);
        cartRepository.save(cart);
        
        // Создаем новую пустую корзину
        Cart newCart = cart.createNewCartAfterOrder(orderId);
        cartRepository.save(newCart);
        
        // Очищаем кеш
        cartCacheService.evictCart(userId);
    }

    // Восстановить последнюю корзину
    public CartResponse restoreLastCart(Long userId) {
        List<Cart> archivedCarts = cartRepository.findByUserIdAndStatusIn(
                userId, List.of(CartStatus.ARCHIVED, CartStatus.CONVERTED_TO_ORDER));
        
        if (archivedCarts.isEmpty()) {
            throw new ResourceNotFoundException("No archived carts found for user: " + userId);
        }
        
        Cart lastCart = archivedCarts.get(0); // Самая последняя
        
        // Создаем новую корзину с товарами из последней
        Cart newCart = Cart.builder()
                .userId(userId)
                .cartItems(new ArrayList<>(lastCart.getCartItems()))
                .total(lastCart.getTotal())
                .status(CartStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .lastActivityAt(Instant.now())
                .build();
        
        // Обновляем связи для новых CartItems
        newCart.getCartItems().forEach(item -> item.setCart(newCart));
        
        Cart savedCart = cartRepository.save(newCart);
        
        // Обновляем кеш
        CartResponse cartResponse = cartMapper.toCartResponse(savedCart);
        cartCacheService.updateCachedCart(userId, cartResponse);
        
        return cartResponse;
    }

    // --- Вспомогательные методы ---
    private Cart getOrCreateActiveCart(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> createNewCart(userId));
    }

    private Cart createNewCart(Long userId) {
        return Cart.builder()
                .userId(userId)
                .cartItems(new ArrayList<>())
                .total(BigDecimal.ZERO)
                .status(CartStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .lastActivityAt(Instant.now())
                .build();
    }

    private Cart getActiveCartOrThrow(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found for user: " + userId));
    }
}
