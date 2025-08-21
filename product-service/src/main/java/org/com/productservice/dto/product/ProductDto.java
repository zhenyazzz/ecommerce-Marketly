package org.com.productservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.com.productservice.model.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private UUID id;

    private String name;

    private String description;

    private BigDecimal price;

    private String mainImage;

    private Category category;

    private Integer stock;

    @Builder.Default
    private List<String> images = new ArrayList<>();

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}


