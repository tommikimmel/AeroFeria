package com.aeroferia.api.specification;

import com.aeroferia.api.dto.CatalogFilterDto;
import com.aeroferia.api.entity.Category;
import com.aeroferia.api.entity.Publication;
import com.aeroferia.api.entity.Store;
import com.aeroferia.api.entity.enums.Currency;
import com.aeroferia.api.entity.enums.ItemCondition;
import com.aeroferia.api.entity.enums.PublicationStatus;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PublicationSpecificationTest {

    @Mock
    private Root<Publication> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<Object> statusPath;

    @Mock
    private Path<Object> locationPath;

    @Mock
    private Path<Object> conditionPath;

    @Mock
    private Path<Object> currencyPath;

    @Mock
    private Path<BigDecimal> pricePath;

    @Mock
    private Path<Object> titlePath;

    @Mock
    private Path<Object> descriptionPath;

    @Mock
    private Path<String> stringPath;

    @Mock
    private Expression<String> lowerExpression;

    @Mock
    private Join<Publication, Category> categoryJoin;

    @Mock
    private Join<Category, Category> parentCategoryJoin;

    @Mock
    private Path<Object> catIdPath;

    @Mock
    private Path<String> catSlugPath;

    @Mock
    private Path<Store> storePath;

    @Mock
    private Path<Object> storeIdPath;

    @Mock
    private Predicate dummyPredicate;

    @Test
    @DisplayName("withFilters should build predicate with null filter")
    void withFilters_nullFilter_shouldIncludeActivePredicateOnly() {
        when(root.get("status")).thenReturn(statusPath);
        when(cb.equal(statusPath, PublicationStatus.ACTIVE)).thenReturn(dummyPredicate);
        when(cb.and(any(Predicate[].class))).thenReturn(dummyPredicate);

        Specification<Publication> spec = PublicationSpecification.withFilters(null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isNotNull();
        verify(cb).equal(statusPath, PublicationStatus.ACTIVE);
    }

    @Test
    @DisplayName("withFilters should build predicates for all filter fields")
    @SuppressWarnings("unchecked")
    void withFilters_allFields_shouldBuildFullPredicates() {
        when(root.get("status")).thenReturn(statusPath);
        when(cb.equal(statusPath, PublicationStatus.ACTIVE)).thenReturn(dummyPredicate);

        // category join
        when(root.<Publication, Category>join(eq("category"), any(JoinType.class))).thenReturn(categoryJoin);
        when(categoryJoin.<Category, Category>join(eq("parent"), any(JoinType.class))).thenReturn(parentCategoryJoin);
        when(categoryJoin.get("id")).thenReturn(catIdPath);
        when(parentCategoryJoin.get("id")).thenReturn(catIdPath);
        when(cb.equal(catIdPath, 1L)).thenReturn(dummyPredicate);

        // category slug
        when(categoryJoin.<String>get("slug")).thenReturn(catSlugPath);
        when(parentCategoryJoin.<String>get("slug")).thenReturn(catSlugPath);
        when(cb.lower(catSlugPath)).thenReturn(lowerExpression);
        when(cb.equal(eq(lowerExpression), eq("aviones"))).thenReturn(dummyPredicate);

        // search
        when(root.<String>get("title")).thenReturn(stringPath);
        when(root.<String>get("description")).thenReturn(stringPath);
        when(cb.lower(stringPath)).thenReturn(lowerExpression);
        when(cb.like(eq(lowerExpression), anyString())).thenReturn(dummyPredicate);

        // province
        when(root.<String>get("locationProvince")).thenReturn(stringPath);
        when(cb.equal(eq(lowerExpression), eq("buenos aires"))).thenReturn(dummyPredicate);

        // condition
        when(root.get("condition")).thenReturn(conditionPath);
        when(cb.equal(conditionPath, ItemCondition.NEW)).thenReturn(dummyPredicate);

        // currency
        when(root.get("currency")).thenReturn(currencyPath);
        when(cb.equal(currencyPath, Currency.USD)).thenReturn(dummyPredicate);

        // price min and max
        when(root.<BigDecimal>get("price")).thenReturn(pricePath);
        when(cb.greaterThanOrEqualTo(eq(pricePath), eq(BigDecimal.valueOf(100)))).thenReturn(dummyPredicate);
        when(cb.lessThanOrEqualTo(eq(pricePath), eq(BigDecimal.valueOf(500)))).thenReturn(dummyPredicate);

        // store
        when(root.<Store>get("store")).thenReturn(storePath);
        when(storePath.get("id")).thenReturn(storeIdPath);
        when(cb.equal(storeIdPath, 2L)).thenReturn(dummyPredicate);
        when(cb.isNotNull(storePath)).thenReturn(dummyPredicate);

        when(cb.or(any(Predicate[].class))).thenReturn(dummyPredicate);
        when(cb.and(any(Predicate[].class))).thenReturn(dummyPredicate);

        CatalogFilterDto fullFilter = CatalogFilterDto.builder()
                .categoryId(1L)
                .categorySlug("aviones")
                .search("motor")
                .province("Buenos Aires")
                .condition(ItemCondition.NEW)
                .currency(Currency.USD)
                .minPrice(BigDecimal.valueOf(100))
                .maxPrice(BigDecimal.valueOf(500))
                .storeId(2L)
                .onlyStores(true)
                .build();

        Specification<Publication> spec = PublicationSpecification.withFilters(fullFilter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isNotNull();
        verify(cb).equal(conditionPath, ItemCondition.NEW);
        verify(cb).equal(currencyPath, Currency.USD);
        verify(cb).greaterThanOrEqualTo(pricePath, BigDecimal.valueOf(100));
        verify(cb).lessThanOrEqualTo(pricePath, BigDecimal.valueOf(500));
        verify(cb).isNotNull(storePath);
    }
}
