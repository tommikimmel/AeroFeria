package com.aeroferia.api.repository;

import com.aeroferia.api.entity.Publication;
import com.aeroferia.api.entity.enums.PublicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    Optional<Publication> findBySlugAndStatus(String slug, PublicationStatus status);

    Page<Publication> findByStatusOrderByCreatedAtDesc(PublicationStatus status, Pageable pageable);

    Page<Publication> findByStoreIdAndStatusOrderByCreatedAtDesc(Long storeId, PublicationStatus status, Pageable pageable);

    Page<Publication> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByStatus(PublicationStatus status);

    @Query("SELECT p FROM Publication p WHERE p.status = 'ACTIVE' " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId OR p.category.parent.id = :categoryId) " +
           "AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Publication> searchActiveCatalog(@Param("categoryId") Long categoryId,
                                          @Param("search") String search,
                                          Pageable pageable);
}
