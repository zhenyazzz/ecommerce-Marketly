package org.com.orderservice.dto.external.cart_service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID cartId,
        Long userId,
        List<CartItemResponse> items,
        BigDecimal total
) {}
