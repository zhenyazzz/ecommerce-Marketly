package org.com.orderservice.dto.request.update;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;



@Data
@Builder
public class UpdateOrderItemRequest {

    @NotNull
    private UUID id; // Идентификатор позиции заказа

    @NotNull
    private UUID productId;

    private String name;

    private BigDecimal price;

    @Min(1)
    private int quantity;


}
