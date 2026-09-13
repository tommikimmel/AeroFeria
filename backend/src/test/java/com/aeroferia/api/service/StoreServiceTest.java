package com.aeroferia.api.service;

import com.aeroferia.api.dto.StoreDto;
import com.aeroferia.api.dto.StorefrontDto;
import com.aeroferia.api.entity.Category;
import com.aeroferia.api.entity.Publication;
import com.aeroferia.api.entity.PublicationImage;
import com.aeroferia.api.entity.Store;
import com.aeroferia.api.entity.enums.Currency;
import com.aeroferia.api.entity.enums.ItemCondition;
import com.aeroferia.api.entity.enums.PublicationStatus;
import com.aeroferia.api.exception.ResourceNotFoundException;
import com.aeroferia.api.repository.PublicationRepository;
import com.aeroferia.api.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private PublicationRepository publicationRepository;

    @InjectMocks
    private StoreService storeService;

    private Store store;
    private Publication publication;

    @BeforeEach
    void setUp() {
        store = Store.builder()
                .id(1L)
                .name("HobbyMotors")
                .slug("hobbymotors")
                .logoUrl("/uploads/stores/hobbymotors-logo.webp")
                .bannerUrl("/uploads/stores/hobbymotors-banner.webp")
                .description("Referente de aeromodelismo")
                .brandsRepresented("Futaba, OS Engines, Spektrum")
                .shipsNationwide(true)
                .addressLine("Av. Libertador 1000")
                .locationProvince("Buenos Aires")
                .locationCity("Vicente López")
                .whatsappNumber("5491100001111")
                .websiteUrl("https://hobbymotors.com.ar")
                .instagramHandle("@hobbymotors")
                .isVerified(true)
                .isActive(true)
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Radios")
                .slug("radios")
                .build();

        PublicationImage img1 = PublicationImage.builder()
                .id(10L)
                .imageUrl("/uploads/img1.webp")
                .isCover(false)
                .build();

        PublicationImage coverImg = PublicationImage.builder()
                .id(11L)
                .imageUrl("/uploads/cover.webp")
                .isCover(true)
                .build();

        publication = Publication.builder()
                .id(100L)
                .title("Radio Futaba 16IZ")
                .slug("radio-futaba-16iz")
                .price(BigDecimal.valueOf(850))
                .currency(Currency.USD)
                .condition(ItemCondition.LIKE_NEW)
                .locationProvince("Buenos Aires")
                .locationCity("Vicente López")
                .category(category)
                .store(store)
                .viewsCount(120)
                .status(PublicationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .images(List.of(img1, coverImg))
                .build();
    }

    @Test
    @DisplayName("getActiveStores should return list of active stores with parsed brands and count")
    void getActiveStores_shouldReturnStores() {
        when(storeRepository.findByIsActiveTrueOrderByIsVerifiedDescNameAsc()).thenReturn(List.of(store));
        when(publicationRepository.countByStoreIdAndStatus(1L, PublicationStatus.ACTIVE)).thenReturn(5L);

        List<StoreDto> result = storeService.getActiveStores();

        assertThat(result).hasSize(1);
        StoreDto dto = result.getFirst();
        assertThat(dto.getName()).isEqualTo("HobbyMotors");
        assertThat(dto.getBrandsRepresented()).containsExactly("Futaba", "OS Engines", "Spektrum");
        assertThat(dto.getActivePublicationsCount()).isEqualTo(5L);
        assertThat(dto.getIsVerified()).isTrue();
    }

    @Test
    @DisplayName("getStorefrontBySlug should return storefront and its active publications")
    void getStorefrontBySlug_shouldReturnStorefront_whenFound() {
        when(storeRepository.findBySlugAndIsActiveTrue("hobbymotors")).thenReturn(Optional.of(store));
        when(publicationRepository.countByStoreIdAndStatus(1L, PublicationStatus.ACTIVE)).thenReturn(1L);
        when(publicationRepository.findByStoreIdAndStatusOrderByCreatedAtDesc(eq(1L), eq(PublicationStatus.ACTIVE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(publication)));

        StorefrontDto storefront = storeService.getStorefrontBySlug("hobbymotors");

        assertThat(storefront).isNotNull();
        assertThat(storefront.getStore().getSlug()).isEqualTo("hobbymotors");
        assertThat(storefront.getPublications()).hasSize(1);
        assertThat(storefront.getPublications().getFirst().getCoverImageUrl()).isEqualTo("/uploads/cover.webp");
        assertThat(storefront.getPublications().getFirst().getIsVerifiedStore()).isTrue();
    }

    @Test
    @DisplayName("getStorefrontBySlug should throw ResourceNotFoundException when slug not found")
    void getStorefrontBySlug_shouldThrowException_whenNotFound() {
        when(storeRepository.findBySlugAndIsActiveTrue("tienda-falsa")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storeService.getStorefrontBySlug("tienda-falsa"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tienda no encontrado");
    }

    @Test
    @DisplayName("mapToDto should handle null or empty brandsRepresented correctly")
    void mapToDto_shouldHandleNullOrEmptyBrands() {
        store.setBrandsRepresented(null);
        StoreDto dto = storeService.mapToDto(store);
        assertThat(dto.getBrandsRepresented()).isEmpty();

        store.setBrandsRepresented("   ");
        StoreDto dto2 = storeService.mapToDto(store);
        assertThat(dto2.getBrandsRepresented()).isEmpty();
    }

    @Test
    @DisplayName("countActiveStores should return total count from storeRepository")
    void countActiveStores_shouldReturnCount() {
        when(storeRepository.count()).thenReturn(2L);
        assertThat(storeService.countActiveStores()).isEqualTo(2L);
    }
}
