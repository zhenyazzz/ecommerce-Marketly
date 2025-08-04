package org.com.orderservice.dto.request.update;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.com.orderservice.dto.request.create.CreateOrderItemRequest;

import java.util.List;


@Data
@Builder
public class UpdateOrderRequest {

        @NotNull
        private Long userId;

        private List<CreateOrderItemRequest> items; // если разрешено менять

        private String shippingAddress;

        private String customerNotes;

        private String paymentMethod;

        private String deliveryType;

        @NotBlank
        private String status; // например: NEW, PAID, CANCELLED



}
