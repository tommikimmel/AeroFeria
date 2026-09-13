package com.aeroferia.api.repository;

import com.aeroferia.api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAsc();
    Optional<Category> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
