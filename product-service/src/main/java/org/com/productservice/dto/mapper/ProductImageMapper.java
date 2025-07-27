package org.com.productservice.dto.mapper;


import org.com.productservice.dto.productImage.ProductImageRequest;
import org.com.productservice.dto.productImage.ProductImageResponse;
import org.com.productservice.model.ProductImage;
import org.springframework.stereotype.Component;

@Component
public class ProductImageMapper {

    public ProductImage toEntity(ProductImageRequest request) {
        return ProductImage.builder()
                .url(request.getUrl())
                .isMain(request.getIsMain())
                .build();
    }

    public ProductImageResponse toResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .url(image.getUrl())
                .isMain(image.getIsMain())
                .build();
    }
}

