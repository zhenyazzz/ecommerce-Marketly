package org.com.productservice.service;

import org.com.productservice.dto.productImage.ProductImageRequest;
import org.com.productservice.dto.productImage.ProductImageResponse;

import java.util.UUID;

public interface IProductImageService {
    ProductImageResponse addImageToProduct(UUID productId, ProductImageRequest request);
    void deleteImage(UUID imageId);
}
