package org.com.orderservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;




@Data
@Builder
public class OrderResponse {
    private UUID id;
    private Long userId;
    private List<OrderItemResponse> items;
    private String status;
    private String shippingAddress;
    private String customerNotes;
    private String paymentMethod;
    private String deliveryType;
    private Instant createdAt;
    private Instant updatedAt;
    private BigDecimal total;

}
