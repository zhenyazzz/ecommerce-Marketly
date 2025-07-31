package org.com.productservice.dto.product;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    // можно отдать URL изображения
    private String mainImage;

    private Long categoryId;

    private Integer stock;

    private List<String> images;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
