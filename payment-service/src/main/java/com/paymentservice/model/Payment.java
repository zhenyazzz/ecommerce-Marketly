package com.paymentservice.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    UUID id;

    @NotNull(message = "order id must be present")
    @Column(name = "orderID")
    UUID orderId;

    @NotNull(message = "user id must be present")
    @Column(name = "userId")
    Long userId;

    @Min(value = 0,message = "amount cant be negative")
    @Column(name = "amount")
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "status must be present")
    @Column(name = "status")
    PaymentStatus status;

    @NotNull(message = "creation time must be present")
    @Column(name = "createdAt")
    Instant createdAt ;

}
