package com.aeroferia.api.repository;

import com.aeroferia.api.entity.Publication;
import com.aeroferia.api.entity.enums.PublicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long>, JpaSpecificationExecutor<Publication> {

    Optional<Publication> findBySlugAndStatus(String slug, PublicationStatus status);

    Optional<Publication> findByIdAndStatus(Long id, PublicationStatus status);

    Page<Publication> findByStatusOrderByCreatedAtDesc(PublicationStatus status, Pageable pageable);

    Page<Publication> findByStoreIdAndStatusOrderByCreatedAtDesc(Long storeId, PublicationStatus status, Pageable pageable);

    Page<Publication> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByStatus(PublicationStatus status);

    long countByStoreIdAndStatus(Long storeId, PublicationStatus status);

    @Modifying
    @Query("UPDATE Publication p SET p.viewsCount = p.viewsCount + 1 WHERE p.id = :id")
    int incrementViewsCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Publication p SET p.whatsappClicksCount = p.whatsappClicksCount + 1 WHERE p.id = :id")
    int incrementWhatsappClicksCount(@Param("id") Long id);
}
