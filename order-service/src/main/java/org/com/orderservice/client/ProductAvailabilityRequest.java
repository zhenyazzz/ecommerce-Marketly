package org.com.orderservice.client;

import java.util.UUID;

public record ProductAvailabilityRequest(
    UUID productId,
    int requiredQuantity
) {

}
