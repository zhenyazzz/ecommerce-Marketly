package com.paymentservice.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(

    UUID orderId,
    Long userId,
    BigDecimal amount

) {
}
