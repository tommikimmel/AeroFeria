package com.aeroferia.api.service;

import com.aeroferia.api.dto.CategoryTreeDto;
import com.aeroferia.api.entity.Category;
import com.aeroferia.api.exception.ResourceNotFoundException;
import com.aeroferia.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryTreeDto> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAsc();
        return rootCategories.stream()
                .map(this::mapToTreeDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "slug", slug));
    }

    @Transactional(readOnly = true)
    public long countActiveCategories() {
        return categoryRepository.count();
    }

    private CategoryTreeDto mapToTreeDto(Category category) {
        List<CategoryTreeDto> subcategories = category.getSubcategories() == null ? List.of() :
                category.getSubcategories().stream()
                        .filter(sub -> Boolean.TRUE.equals(sub.getIsActive()))
                        .sorted(Comparator.comparingInt(Category::getDisplayOrder))
                        .map(this::mapToTreeDto)
                        .toList();

        return CategoryTreeDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .iconName(category.getIconName())
                .displayOrder(category.getDisplayOrder())
                .subcategories(subcategories)
                .build();
    }
}
