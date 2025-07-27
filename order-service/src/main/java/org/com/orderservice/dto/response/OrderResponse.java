package org.com.orderservice.dto.response;

import org.com.orderservice.model.DeliveryType;
import org.com.orderservice.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        UUID userId,
        String status,
        BigDecimal totalAmount,
        Instant createdAt,
        String shippingAddress,
        String paymentMethod,
        String deliveryType,
        List<OrderItemDto> items
) {

}
