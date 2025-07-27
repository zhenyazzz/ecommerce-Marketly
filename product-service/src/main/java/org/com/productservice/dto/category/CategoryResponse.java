package org.com.productservice.dto.category;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private UUID id;
    private String name;
    private UUID parentId;
    private String parentName;
    private int productCount;
}
