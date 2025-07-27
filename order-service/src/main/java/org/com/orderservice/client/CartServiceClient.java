package org.com.orderservice.client;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.com.orderservice.dto.external.cart_service.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "cart-service",
        fallback = CartServiceClientFallback.class
)
public interface CartServiceClient {
    @Operation(summary = "Get user cart")
    @GetMapping("/api/cart")
    CartResponse getCart(@RequestHeader("X-User-Id") @NotNull UUID userId);

    @Operation(summary = "Clear user cart")
    @DeleteMapping("/api/cart")
    void clearCart(@RequestHeader("X-User-Id") @NotNull UUID userId);
}
