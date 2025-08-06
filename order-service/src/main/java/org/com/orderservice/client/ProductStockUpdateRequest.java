package org.com.orderservice.client;

import java.util.UUID;

public record ProductStockUpdateRequest(
    UUID productId,
    int quantity
) {

}
