package org.com.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCartRequest(
        @NotNull UUID productId,
        @Min(1) int newQuantity
) {}
