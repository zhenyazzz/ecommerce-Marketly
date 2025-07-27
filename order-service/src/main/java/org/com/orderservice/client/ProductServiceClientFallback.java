package org.com.orderservice.client;

import lombok.extern.slf4j.Slf4j;
import org.com.orderservice.dto.external.product_service.ProductResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;


@Component
@Slf4j
public class ProductServiceClientFallback implements ProductServiceClient {
    @Override
    public void updateStock(UUID productId, int quantity) {

    }

    @Override
    public ProductResponseDto checkAvailability(UUID productId, int requiredQuantity) {
        log.error("Fallback: ProductService unavailable");
        return new ProductResponseDto(null,null, BigDecimal.ZERO,0);
    }
}
