package org.com.orderservice.client;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @Operation(summary = "Update stock batch")
    @PostMapping("/api/products/stock/batch")
    void updateStockBatch(@RequestBody List<ProductStockUpdateRequest> updates);

    @Operation(summary = "Return stock batch")
    @PostMapping("/api/products/stock/batch/return")
    void returnStockBatch(@RequestBody List<ProductStockUpdateRequest> updates);

	@Operation(summary = "Check product availability batch")
	@PostMapping("/api/products/availability/batch")
	List<ProductAvailabilityResponse> checkAvailabilityBatch(@RequestBody List<ProductAvailabilityRequest> requests);

    @Operation(summary = "Update stock")
    @PostMapping("/api/products/stock")
    void updateStock(@RequestBody ProductStockUpdateRequest request);

	@Operation(summary = "Check product availability")
	@PostMapping("/api/products/availability")
	ProductAvailabilityResponse checkAvailability(@RequestBody ProductAvailabilityRequest request);




}
