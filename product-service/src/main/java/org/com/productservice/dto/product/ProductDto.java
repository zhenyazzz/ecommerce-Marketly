package org.com.productservice.dto.product;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.com.productservice.model.Category;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto{
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private String mainImage;

    private Category category;

    private Integer stock;

    private List<String> images = new ArrayList<>();

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}


