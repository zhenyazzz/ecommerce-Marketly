package org.com.productservice.dto.product;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be 3-100 characters")
    private String name;

    @Size(max = 1000, message = "Description too long")
    private String description;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    // ID изображения, которое станет основным
    private String mainImage;

    // список ID изображений
    private List<String> images;

    private boolean active;
}
