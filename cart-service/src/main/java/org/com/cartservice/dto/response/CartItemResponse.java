package org.com.cartservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID productId,
        String name,
        BigDecimal price,
        int quantity
) {}
