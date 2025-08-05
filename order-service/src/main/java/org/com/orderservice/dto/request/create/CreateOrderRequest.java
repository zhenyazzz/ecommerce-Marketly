package org.com.orderservice.dto.request.create;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class CreateOrderRequest {

        @NotNull
        private Long userId;

        @NotNull
        @Size(min = 1, message = "Order must contain at least one item")
        private List<CreateOrderItemRequest> items;

        @NotBlank
        private String shippingAddress;

        private String customerNotes;

        @NotBlank(message = "Payment method is required")
        private String paymentMethod;

        @NotBlank(message = "Delivery type is required")
        private String deliveryType;


}
