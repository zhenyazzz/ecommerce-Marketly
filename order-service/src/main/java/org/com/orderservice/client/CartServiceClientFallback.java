package org.com.orderservice.client;

import lombok.extern.slf4j.Slf4j;
import org.com.orderservice.dto.external.cart_service.CartResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class CartServiceClientFallback implements CartServiceClient {
    @Override
    public CartResponse getCart(UUID userId) {
        log.error("Fallback: CartService unavailable");
        return new CartResponse(null, userId, List.of(),BigDecimal.ZERO);
    }

    @Override
    public void clearCart(UUID userId) {
        log.error("Fallback: Cart clearance failed");
    }
}
