package org.com.productservice.dto.productImage;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {
    @NotBlank(message = "URL is required")
    @URL(message = "Invalid URL format")
    private String url;

    @NotNull(message = "isMain flag is required")
    private Boolean isMain;
}
