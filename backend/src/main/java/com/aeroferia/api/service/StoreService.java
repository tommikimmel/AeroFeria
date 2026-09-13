package com.aeroferia.api.service;

import com.aeroferia.api.dto.PublicationCardDto;
import com.aeroferia.api.dto.StoreDto;
import com.aeroferia.api.dto.StorefrontDto;
import com.aeroferia.api.entity.Publication;
import com.aeroferia.api.entity.PublicationImage;
import com.aeroferia.api.entity.Store;
import com.aeroferia.api.entity.enums.PublicationStatus;
import com.aeroferia.api.exception.ResourceNotFoundException;
import com.aeroferia.api.repository.PublicationRepository;
import com.aeroferia.api.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final PublicationRepository publicationRepository;

    @Transactional(readOnly = true)
    public List<StoreDto> getActiveStores() {
        List<Store> stores = storeRepository.findByIsActiveTrueOrderByIsVerifiedDescNameAsc();
        return stores.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public StorefrontDto getStorefrontBySlug(String slug) {
        Store store = storeRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tienda", "slug", slug));

        StoreDto storeDto = mapToDto(store);

        List<PublicationCardDto> publications = publicationRepository
                .findByStoreIdAndStatusOrderByCreatedAtDesc(store.getId(), PublicationStatus.ACTIVE, PageRequest.of(0, 50))
                .getContent()
                .stream()
                .map(this::mapPublicationToCardDto)
                .toList();

        return StorefrontDto.builder()
                .store(storeDto)
                .publications(publications)
                .build();
    }

    @Transactional(readOnly = true)
    public long countActiveStores() {
        return storeRepository.count();
    }

    public StoreDto mapToDto(Store store) {
        List<String> brands = Collections.emptyList();
        if (store.getBrandsRepresented() != null && !store.getBrandsRepresented().isBlank()) {
            brands = Arrays.stream(store.getBrandsRepresented().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        long activeCount = publicationRepository.countByStoreIdAndStatus(store.getId(), PublicationStatus.ACTIVE);

        return StoreDto.builder()
                .id(store.getId())
                .name(store.getName())
                .slug(store.getSlug())
                .logoUrl(store.getLogoUrl())
                .bannerUrl(store.getBannerUrl())
                .description(store.getDescription())
                .brandsRepresented(brands)
                .shipsNationwide(store.getShipsNationwide())
                .addressLine(store.getAddressLine())
                .locationProvince(store.getLocationProvince())
                .locationCity(store.getLocationCity())
                .whatsappNumber(store.getWhatsappNumber())
                .websiteUrl(store.getWebsiteUrl())
                .instagramHandle(store.getInstagramHandle())
                .isVerified(store.getIsVerified())
                .activePublicationsCount(activeCount)
                .build();
    }

    private PublicationCardDto mapPublicationToCardDto(Publication pub) {
        String coverUrl = null;
        if (pub.getImages() != null && !pub.getImages().isEmpty()) {
            coverUrl = pub.getImages().stream()
                    .filter(PublicationImage::getIsCover)
                    .findFirst()
                    .map(PublicationImage::getImageUrl)
                    .orElse(pub.getImages().getFirst().getImageUrl());
        }

        return PublicationCardDto.builder()
                .id(pub.getId())
                .title(pub.getTitle())
                .slug(pub.getSlug())
                .coverImageUrl(coverUrl)
                .price(pub.getPrice())
                .currency(pub.getCurrency())
                .condition(pub.getCondition())
                .locationProvince(pub.getLocationProvince())
                .locationCity(pub.getLocationCity())
                .categoryId(pub.getCategory() != null ? pub.getCategory().getId() : null)
                .categoryName(pub.getCategory() != null ? pub.getCategory().getName() : null)
                .storeId(pub.getStore() != null ? pub.getStore().getId() : null)
                .storeName(pub.getStore() != null ? pub.getStore().getName() : null)
                .storeSlug(pub.getStore() != null ? pub.getStore().getSlug() : null)
                .isVerifiedStore(pub.getStore() != null && Boolean.TRUE.equals(pub.getStore().getIsVerified()))
                .viewsCount(pub.getViewsCount())
                .createdAt(pub.getCreatedAt())
                .build();
    }
}
