package org.com.productservice.dto.category;

import java.util.List;

public record CategoryTreeResponse(
        CategoryResponse category,
        List<CategoryTreeResponse> children
) {
}
