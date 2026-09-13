package com.aeroferia.api.repository;

import com.aeroferia.api.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findBySlug(String slug);
    Optional<Store> findBySlugAndIsActiveTrue(String slug);
    Optional<Store> findByUserId(Long userId);
    List<Store> findByIsActiveTrueOrderByIsVerifiedDescNameAsc();
    boolean existsBySlug(String slug);
}
