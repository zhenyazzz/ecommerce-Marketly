package org.com.orderservice.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(
        @NotNull UUID productId,
        @NotNull String name,
        @NotNull @Positive BigDecimal price,
        @NotNull @Min(1) Integer quantity
) {
}
