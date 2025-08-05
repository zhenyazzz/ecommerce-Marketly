package org.com.orderservice.client;

import lombok.extern.slf4j.Slf4j;
import org.com.orderservice.dto.external.product_service.ProductResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Component
@Slf4j
public class ProductServiceClientFallback implements ProductServiceClient {

    @Override
    public void updateStockBatch(List<ProductStockUpdateRequest> updates) {
        log.info("update stock batch exception");
    }

    @Override
    public void returnStockBatch(List<ProductStockUpdateRequest> updates) {
        log.info("return stock batch exception");
    }

    @Override
    public List<ProductAvailabilityResponse> checkAvailabilityBatch(List<ProductAvailabilityRequest> requests) {
        log.info("check availability batch exception");
        return List.of();
    }

    @Override
    public void updateStock(ProductStockUpdateRequest request) {
        log.info("update stock exception");
    }

    @Override
    public ProductAvailabilityResponse checkAvailability(ProductAvailabilityRequest request) {
        log.info("check availability exception");
        return null;
    }
}
