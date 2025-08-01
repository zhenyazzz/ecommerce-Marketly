package org.com.cartservice.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.cartservice.kafka.event.UserCreatedEvent;
import org.com.cartservice.model.Cart;
import org.com.cartservice.model.CartStatus;
import org.com.cartservice.repository.CartRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedConsumer {

    private final CartRepository cartRepository;

    @KafkaListener(topics = "user-created", groupId = "cart-service")
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Received UserCreatedEvent: {}", event);
        
        try {
            // Создаем корзину для нового пользователя
            Cart cart = Cart.builder()
                    .id(UUID.randomUUID())
                    .userId(event.getUserId())
                    .cartItems(new ArrayList<>())
                    .total(BigDecimal.ZERO)
                    .status(CartStatus.ACTIVE)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .lastActivityAt(Instant.now())
                    .build();
            
            cartRepository.save(cart);
            log.info("Created active cart for user: {}", event.getUserId());
            
        } catch (Exception e) {
            log.error("Error creating cart for user {}: {}", event.getUserId(), e.getMessage(), e);
            
        }
    }
} 