package org.com.orderservice.dto.external.product_service;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDto(
        UUID id,
        String name,
        BigDecimal price,
        int availableStock // Доступное количество
) {}
