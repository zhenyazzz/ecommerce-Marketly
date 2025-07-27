package org.com.orderservice.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.com.orderservice.model.DeliveryType;
import org.com.orderservice.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderRequest(
        @NotNull(message = "Customer ID is required")
        UUID customerId,

        @NotNull(message = "Shipping address is required")
        String shippingAddress,

        String customerNotes,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        DeliveryType deliveryType
) {
}
