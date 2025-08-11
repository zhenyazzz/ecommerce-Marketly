package com.paymentservice.event;

import lombok.Getter;
import org.com.orderservice.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;



public record OrderPaymentEvent(
        UUID orderId,
        Long userId,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {

}