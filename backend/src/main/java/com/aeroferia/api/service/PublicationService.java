package com.aeroferia.api.service;

import com.aeroferia.api.dto.*;
import com.aeroferia.api.entity.Publication;
import com.aeroferia.api.entity.PublicationImage;
import com.aeroferia.api.entity.Store;
import com.aeroferia.api.entity.User;
import com.aeroferia.api.entity.enums.PublicationStatus;
import com.aeroferia.api.exception.ResourceNotFoundException;
import com.aeroferia.api.repository.CategoryRepository;
import com.aeroferia.api.repository.PublicationRepository;
import com.aeroferia.api.repository.StoreRepository;
import com.aeroferia.api.repository.UserRepository;
import com.aeroferia.api.specification.PublicationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PublicationCardDto> getCatalog(CatalogFilterDto filter, Pageable pageable) {
        Pageable effectivePageable = resolvePageableWithSort(filter, pageable);
        Page<Publication> page = publicationRepository.findAll(
                PublicationSpecification.withFilters(filter),
                effectivePageable
        );
        return page.map(this::mapToCardDto);
    }

    @Transactional
    public PublicationDetailDto getBySlugOrId(String slugOrId) {
        Publication pub;
        if (slugOrId.matches("^\\d+$")) {
            Long id = Long.parseLong(slugOrId);
            pub = publicationRepository.findByIdAndStatus(id, PublicationStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("Publicación", "id", id));
        } else {
            pub = publicationRepository.findBySlugAndStatus(slugOrId, PublicationStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("Publicación", "slug", slugOrId));
        }

        publicationRepository.incrementViewsCount(pub.getId());
        pub.setViewsCount(pub.getViewsCount() + 1);

        return mapToDetailDto(pub);
    }

    @Transactional
    public WhatsAppClickResponseDto recordWhatsAppClick(Long publicationId) {
        Publication pub = publicationRepository.findByIdAndStatus(publicationId, PublicationStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Publicación", "id", publicationId));

        publicationRepository.incrementWhatsappClicksCount(publicationId);
        int updatedClicks = pub.getWhatsappClicksCount() + 1;
        pub.setWhatsappClicksCount(updatedClicks);

        String targetPhone = resolveSellerPhoneNumber(pub);
        String cleanPhone = targetPhone.replaceAll("[^0-9]", "");

        String message = String.format("¡Hola! Te consulto por tu aviso en AeroFeria: \"%s\" (%s %s). ¿Sigue disponible?",
                pub.getTitle(), pub.getCurrency(), pub.getPrice());
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String whatsappUrl = "https://wa.me/" + cleanPhone + "?text=" + encodedMessage;

        return WhatsAppClickResponseDto.builder()
                .publicationId(publicationId)
                .whatsappUrl(whatsappUrl)
                .totalClicks(updatedClicks)
                .targetPhoneNumber(cleanPhone)
                .message(message)
                .build();
    }

    @Transactional(readOnly = true)
    public PublicStatsDto getPublicStats() {
        return PublicStatsDto.builder()
                .totalPublications(publicationRepository.countByStatus(PublicationStatus.ACTIVE))
                .totalStores(storeRepository.count())
                .totalCategories(categoryRepository.count())
                .totalUsers(userRepository.count())
                .build();
    }

    private Pageable resolvePageableWithSort(CatalogFilterDto filter, Pageable pageable) {
        if (filter == null || filter.getSort() == null || filter.getSort().isBlank()) {
            return pageable;
        }

        Sort sort = switch (filter.getSort().toLowerCase()) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "popular" -> Sort.by(Sort.Direction.DESC, "viewsCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public PublicationCardDto mapToCardDto(Publication pub) {
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

    public PublicationDetailDto mapToDetailDto(Publication pub) {
        List<PublicationImageDto> imageDtos = pub.getImages() == null ? List.of() :
                pub.getImages().stream()
                        .map(img -> PublicationImageDto.builder()
                                .id(img.getId())
                                .imageUrl(img.getImageUrl())
                                .thumbnailUrl(img.getThumbnailUrl())
                                .isCover(img.getIsCover())
                                .displayOrder(img.getDisplayOrder())
                                .build())
                        .toList();

        User user = pub.getUser();
        Store store = pub.getStore();

        SellerInfoDto sellerDto = SellerInfoDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .phoneNumber(user.getPhoneNumber())
                .locationProvince(user.getLocationProvince())
                .locationCity(user.getLocationCity())
                .isStore(store != null)
                .storeId(store != null ? store.getId() : null)
                .storeName(store != null ? store.getName() : null)
                .storeSlug(store != null ? store.getSlug() : null)
                .storeLogoUrl(store != null ? store.getLogoUrl() : null)
                .isVerifiedStore(store != null && Boolean.TRUE.equals(store.getIsVerified()))
                .build();

        String targetPhone = resolveSellerPhoneNumber(pub);
        String cleanPhone = targetPhone.replaceAll("[^0-9]", "");
        String message = String.format("¡Hola! Te consulto por tu aviso en AeroFeria: \"%s\" (%s %s). ¿Sigue disponible?",
                pub.getTitle(), pub.getCurrency(), pub.getPrice());
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String directWhatsappUrl = "https://wa.me/" + cleanPhone + "?text=" + encodedMessage;

        return PublicationDetailDto.builder()
                .id(pub.getId())
                .title(pub.getTitle())
                .slug(pub.getSlug())
                .description(pub.getDescription())
                .condition(pub.getCondition())
                .price(pub.getPrice())
                .currency(pub.getCurrency())
                .videoUrl(pub.getVideoUrl())
                .locationProvince(pub.getLocationProvince())
                .locationCity(pub.getLocationCity())
                .status(pub.getStatus())
                .viewsCount(pub.getViewsCount())
                .whatsappClicksCount(pub.getWhatsappClicksCount())
                .categoryId(pub.getCategory() != null ? pub.getCategory().getId() : null)
                .categoryName(pub.getCategory() != null ? pub.getCategory().getName() : null)
                .categorySlug(pub.getCategory() != null ? pub.getCategory().getSlug() : null)
                .images(imageDtos)
                .seller(sellerDto)
                .whatsappUrl(directWhatsappUrl)
                .createdAt(pub.getCreatedAt())
                .updatedAt(pub.getUpdatedAt())
                .build();
    }

    private String resolveSellerPhoneNumber(Publication pub) {
        if (pub.getStore() != null && pub.getStore().getWhatsappNumber() != null && !pub.getStore().getWhatsappNumber().isBlank()) {
            return pub.getStore().getWhatsappNumber();
        }
        return pub.getUser().getPhoneNumber();
    }
}
