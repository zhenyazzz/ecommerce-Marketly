package com.paymentservice.event;


import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

//TODO: заглушка
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private List<OrderItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private Long productId;
        private Integer quantity;
        private BigDecimal pricePerUnit;
    }
}
