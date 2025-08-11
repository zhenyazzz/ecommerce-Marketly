package com.paymentservice.dto;

import com.paymentservice.model.PaymentMethod;
import com.paymentservice.model.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PaymentResponse {
    UUID id;
    UUID orderId;
    Long userId;
    BigDecimal amount;
    PaymentStatus status;
    private PaymentMethod paymentMethod;
    Instant createdAt ;
}
