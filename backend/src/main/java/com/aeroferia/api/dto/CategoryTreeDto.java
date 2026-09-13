package com.aeroferia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String iconName;
    private Integer displayOrder;
    @Builder.Default
    private List<CategoryTreeDto> subcategories = new ArrayList<>();
}
