package org.com.productservice.dto.productImage;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponse {
    private UUID id;
    private String url;
    private Boolean isMain;
}
