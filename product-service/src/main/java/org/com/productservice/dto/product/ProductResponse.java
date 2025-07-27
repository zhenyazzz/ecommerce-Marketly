package org.com.productservice.dto.product;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private Integer stock;
    private UUID categoryId;
    private String categoryName;
}
