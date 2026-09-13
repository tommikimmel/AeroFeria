package com.aeroferia.api.service;

import com.aeroferia.api.dto.CategoryTreeDto;
import com.aeroferia.api.entity.Category;
import com.aeroferia.api.exception.ResourceNotFoundException;
import com.aeroferia.api.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category parentCategory;
    private Category subCategory1;
    private Category subCategory2;

    @BeforeEach
    void setUp() {
        parentCategory = Category.builder()
                .id(1L)
                .name("Aviones")
                .slug("aviones")
                .description("Modelos de alas fijas")
                .iconName("plane")
                .displayOrder(1)
                .isActive(true)
                .subcategories(new ArrayList<>())
                .build();

        subCategory1 = Category.builder()
                .id(2L)
                .name("Entrenadores")
                .slug("aviones-entrenadores")
                .parent(parentCategory)
                .displayOrder(1)
                .isActive(true)
                .build();

        subCategory2 = Category.builder()
                .id(3L)
                .name("Inactivo")
                .slug("aviones-inactivo")
                .parent(parentCategory)
                .displayOrder(2)
                .isActive(false)
                .build();

        parentCategory.getSubcategories().add(subCategory1);
        parentCategory.getSubcategories().add(subCategory2);
    }

    @Test
    @DisplayName("getCategoryTree should return active root categories with active sorted subcategories")
    void getCategoryTree_shouldReturnActiveRootsAndActiveSubcategories() {
        when(categoryRepository.findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAsc())
                .thenReturn(List.of(parentCategory));

        List<CategoryTreeDto> tree = categoryService.getCategoryTree();

        assertThat(tree).hasSize(1);
        CategoryTreeDto root = tree.getFirst();
        assertThat(root.getId()).isEqualTo(1L);
        assertThat(root.getName()).isEqualTo("Aviones");
        assertThat(root.getSubcategories()).hasSize(1);
        assertThat(root.getSubcategories().getFirst().getName()).isEqualTo("Entrenadores");

        verify(categoryRepository, times(1)).findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAsc();
    }

    @Test
    @DisplayName("getCategoryTree should handle null subcategories gracefully")
    void getCategoryTree_shouldHandleNullSubcategories() {
        Category singleCat = Category.builder()
                .id(10L)
                .name("Solo")
                .slug("solo")
                .subcategories(null)
                .isActive(true)
                .build();

        when(categoryRepository.findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAsc())
                .thenReturn(List.of(singleCat));

        List<CategoryTreeDto> tree = categoryService.getCategoryTree();

        assertThat(tree).hasSize(1);
        assertThat(tree.getFirst().getSubcategories()).isEmpty();
    }

    @Test
    @DisplayName("getCategoryBySlug should return category when found")
    void getCategoryBySlug_shouldReturnCategory_whenFound() {
        when(categoryRepository.findBySlug("aviones")).thenReturn(Optional.of(parentCategory));

        Category result = categoryService.getCategoryBySlug("aviones");

        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("aviones");
        verify(categoryRepository).findBySlug("aviones");
    }

    @Test
    @DisplayName("getCategoryBySlug should throw ResourceNotFoundException when slug does not exist")
    void getCategoryBySlug_shouldThrowException_whenNotFound() {
        when(categoryRepository.findBySlug("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryBySlug("inexistente"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoría no encontrado");
    }

    @Test
    @DisplayName("countActiveCategories should delegate to repository count")
    void countActiveCategories_shouldReturnCount() {
        when(categoryRepository.count()).thenReturn(15L);

        long count = categoryService.countActiveCategories();

        assertThat(count).isEqualTo(15L);
        verify(categoryRepository).count();
    }
}
