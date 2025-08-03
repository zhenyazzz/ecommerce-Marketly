package org.com.orderservice.dto.response;

import org.com.orderservice.model.DeliveryType;
import org.com.orderservice.model.OrderStatus;
import org.com.orderservice.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        Long userId,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        String shippingAddress,
        PaymentMethod paymentMethod,
        DeliveryType deliveryType,
        List<OrderItemResponse> items
) {

}
