package org.com.productservice.mapper;

import org.com.productservice.dto.product.ProductDto;
import org.com.productservice.dto.product.ProductRequest;
import org.com.productservice.dto.product.ProductResponse;
import org.com.productservice.model.Product;
import org.springframework.stereotype.Component;

@Component
public interface ProductMapper {

    Product toProduct(ProductRequest request);

    ProductResponse toProductResponse(Product product) ;

    ProductDto toProductDto(Product product);

    void updateProductFromProductRequest(ProductRequest request, Product product) ;
}
