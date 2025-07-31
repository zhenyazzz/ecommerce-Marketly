package org.com.productservice.mapper;

import org.com.productservice.dto.category.CategoryRequest;
import org.com.productservice.dto.category.CategoryResponse;
import org.com.productservice.model.Category;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring",imports = {Instant.class, Collectors.class})
public interface CategoryMapper {

     Category toCategory(CategoryRequest request);

     CategoryResponse toCategoryResponse(Category category);
}
