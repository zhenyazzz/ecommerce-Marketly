package org.com.orderservice.kafka;

import java.math.BigDecimal;
import java.util.UUID;

import org.com.orderservice.model.PaymentMethod;

public record OrderPaymentEvent(
    UUID orderId,
    Long userId,
    BigDecimal total,
    PaymentMethod paymentMethod
) {

}
