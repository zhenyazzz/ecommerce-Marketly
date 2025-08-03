package org.com.productservice.mapper;

import org.com.productservice.dto.category.CategoryRequest;
import org.com.productservice.dto.category.CategoryResponse;
import org.com.productservice.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryRequest request);

    CategoryResponse toCategoryResponse(Category category);
}
