package com.aeroferia.api.specification;

import com.aeroferia.api.dto.CatalogFilterDto;
import com.aeroferia.api.entity.Category;
import com.aeroferia.api.entity.Publication;
import com.aeroferia.api.entity.enums.PublicationStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PublicationSpecification {

    public static Specification<Publication> withFilters(CatalogFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Solo avisos activos
            predicates.add(cb.equal(root.get("status"), PublicationStatus.ACTIVE));

            if (filter == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            Join<Publication, Category> categoryJoin = null;
            Join<Category, Category> parentCategoryJoin = null;

            // Filtro por ID de categoría o subcategoría
            if (filter.getCategoryId() != null) {
                categoryJoin = root.join("category", JoinType.LEFT);
                parentCategoryJoin = categoryJoin.join("parent", JoinType.LEFT);
                predicates.add(cb.or(
                        cb.equal(categoryJoin.get("id"), filter.getCategoryId()),
                        cb.equal(parentCategoryJoin.get("id"), filter.getCategoryId())
                ));
            }

            // Filtro por slug de categoría o subcategoría
            if (filter.getCategorySlug() != null && !filter.getCategorySlug().isBlank()) {
                if (categoryJoin == null) {
                    categoryJoin = root.join("category", JoinType.LEFT);
                    parentCategoryJoin = categoryJoin.join("parent", JoinType.LEFT);
                }
                String slug = filter.getCategorySlug().trim().toLowerCase();
                predicates.add(cb.or(
                        cb.equal(cb.lower(categoryJoin.get("slug")), slug),
                        cb.equal(cb.lower(parentCategoryJoin.get("slug")), slug)
                ));
            }

            // Búsqueda por término (título o descripción)
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String searchPattern = "%" + filter.getSearch().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), searchPattern),
                        cb.like(cb.lower(root.get("description")), searchPattern)
                ));
            }

            // Filtro por provincia
            if (filter.getProvince() != null && !filter.getProvince().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("locationProvince")), filter.getProvince().trim().toLowerCase()));
            }

            // Filtro por condición
            if (filter.getCondition() != null) {
                predicates.add(cb.equal(root.get("condition"), filter.getCondition()));
            }

            // Filtro por moneda
            if (filter.getCurrency() != null) {
                predicates.add(cb.equal(root.get("currency"), filter.getCurrency()));
            }

            // Filtro por precio mínimo
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }

            // Filtro por precio máximo
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            // Filtro por tienda específica
            if (filter.getStoreId() != null) {
                predicates.add(cb.equal(root.get("store").get("id"), filter.getStoreId()));
            }

            // Filtro para ver únicamente publicaciones de tiendas oficiales o particulares
            if (filter.getOnlyStores() != null) {
                if (filter.getOnlyStores()) {
                    predicates.add(cb.isNotNull(root.get("store")));
                } else {
                    predicates.add(cb.isNull(root.get("store")));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
