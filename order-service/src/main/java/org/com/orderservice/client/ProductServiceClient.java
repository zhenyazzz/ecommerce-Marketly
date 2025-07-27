package org.com.orderservice.client;

import io.swagger.v3.oas.annotations.Operation;
import org.com.orderservice.dto.external.product_service.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @Operation(summary = "Update stock")
    @PutMapping("/api/products/{productId}/stock")
    void updateStock(
            @PathVariable UUID productId,
            @RequestParam int quantity
    );
    @Operation(summary = "Check product availability")
    @GetMapping("/api/products/{productId}/availability")
    ProductResponseDto checkAvailability(
            @PathVariable UUID productId,
            @RequestParam int requiredQuantity
    );
}
