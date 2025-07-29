package org.com.cartservice.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.com.cartservice.model.CartStatus;

public record CartResponse(
        UUID id,
        List<CartItemResponse> cartItems,
        BigDecimal total,
        CartStatus status
) {}
