package org.com.productservice.mapper;

import org.com.productservice.dto.product.ProductDto;
import org.com.productservice.dto.product.ProductRequest;
import org.com.productservice.dto.product.ProductResponse;
import org.com.productservice.model.Category;
import org.com.productservice.model.Product;
import org.com.productservice.repository.CategoryRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Autowired  // Внедряем репозиторий
    protected CategoryRepository categoryRepository;

    @Mapping(target = "category", expression = "java(getCategoryById(request.getCategoryId()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract Product toProduct(ProductRequest request);

    @Mapping(source = "category.id", target = "categoryId")
    public abstract ProductResponse toProductResponse(Product product);

    public abstract ProductDto toProductDto(Product product);

    @Mapping(target = "category", expression = "java(getCategoryById(request.getCategoryId()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "images", qualifiedByName = "updateImages", ignore = true)
    public abstract void updateProductFromProductRequest(ProductRequest request, @MappingTarget Product product);

    // Метод для поиска категории
    protected Category getCategoryById(Long id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Named("updateImages")
    protected List<String> updateImages(List<String> source, @MappingTarget List<String> target) {
        if (target == null) {
            target = new ArrayList<>();
        } else {
            target.clear();
        }
        if (source != null) {
            target.addAll(source);
        }
        return target;
    }

}