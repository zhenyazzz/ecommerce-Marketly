package org.com.orderservice.dto.request.create;

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
public class CreateOrderItemRequest {

    @NotNull
    private UUID productId;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;


}
