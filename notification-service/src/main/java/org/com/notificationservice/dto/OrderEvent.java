package org.com.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private UUID orderId;
    private Long userId;
    private String userEmail;
    private String userName;
    private String orderStatus;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String orderNumber;
    private String previousStatus;
    private String newStatus;
} 