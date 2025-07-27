package org.com.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.productservice.dto.productImage.ProductImageRequest;
import org.com.productservice.dto.productImage.ProductImageResponse;
import org.com.productservice.service.impl.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
@Slf4j
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping("/{productId}")
    public ResponseEntity<ProductImageResponse> addImage(@PathVariable UUID productId,
                                                         @RequestBody @Valid ProductImageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productImageService.addImageToProduct(productId, request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductImageResponse>> getImages(@PathVariable UUID productId) {
        return ResponseEntity.ok(productImageService.getImagesByProductId(productId));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID imageId) {
        productImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<ProductImageResponse> updateImage(@PathVariable UUID imageId,
                                                            @RequestBody @Valid ProductImageRequest request) {
        return ResponseEntity.ok(productImageService.updateImage(imageId, request));
    }
}

