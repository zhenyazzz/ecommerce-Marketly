package org.com.orderservice.dto.request;

import org.com.orderservice.dto.response.OrderItemResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UpdateOrderRequest(
        String shippingAddress,
        String customerNotes,
        String paymentMethod,
        String deliveryType
) {

}
