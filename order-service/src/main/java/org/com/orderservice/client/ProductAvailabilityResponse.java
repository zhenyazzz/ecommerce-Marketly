package org.com.orderservice.client;

import java.util.UUID;

public record ProductAvailabilityResponse(
    UUID productId,
    boolean isAvailable,
    int availableQuantity
) {

}
