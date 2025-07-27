package org.com.orderservice.dto.external.cart_service;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID productId,
        String name,
        BigDecimal price,
        int quantity
) {
}
