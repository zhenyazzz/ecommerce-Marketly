package org.com.productservice.mapper;

import org.com.productservice.dto.product.ProductDto;
import org.com.productservice.dto.product.ProductRequest;
import org.com.productservice.dto.product.ProductResponse;
import org.com.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring",imports = {Instant.class, Collectors.class})
public interface ProductMapper {

    Product toProduct(ProductRequest request);

    ProductResponse toProductResponse(Product product) ;

    ProductDto toProductDto(Product product);

    void updateProductFromProductRequest(ProductRequest request, @MappingTarget Product product) ;
}
