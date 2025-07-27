package org.com.cartservice.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record AddItemRequest(
        @NotNull(message = "Product ID is required")
        UUID productId,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity
) {}