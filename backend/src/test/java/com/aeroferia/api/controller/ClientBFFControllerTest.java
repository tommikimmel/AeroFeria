package com.aeroferia.api.controller;

import com.aeroferia.api.dto.*;
import com.aeroferia.api.entity.enums.Currency;
import com.aeroferia.api.entity.enums.ItemCondition;
import com.aeroferia.api.service.CategoryService;
import com.aeroferia.api.service.PublicationService;
import com.aeroferia.api.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientBFFControllerTest {

    @Mock
    private PublicationService publicationService;

    @Mock
    private StoreService storeService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ClientBFFController controller;

    @Test
    @DisplayName("getPublications should call publicationService with mapped filter and pageable")
    void getPublications_shouldCallService() {
        Page<PublicationCardDto> page = new PageImpl<>(List.of(
                PublicationCardDto.builder().id(1L).title("Avión RC").build()
        ));
        when(publicationService.getCatalog(any(CatalogFilterDto.class), any(Pageable.class)))
                .thenReturn(page);

        ResponseEntity<Page<PublicationCardDto>> response = controller.getPublications(
                1L, "aviones", "motor", "Buenos Aires", ItemCondition.LIKE_NEW,
                BigDecimal.valueOf(100), BigDecimal.valueOf(500), Currency.USD,
                2L, true, "price_asc", 0, 20
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(1);
        verify(publicationService).getCatalog(any(CatalogFilterDto.class), any(Pageable.class));
    }

    @Test
    @DisplayName("getPublicationBySlugOrId should return publication detail")
    void getPublicationBySlugOrId_shouldReturnDetail() {
        PublicationDetailDto detail = PublicationDetailDto.builder()
                .id(1L)
                .title("Avión RC")
                .slug("avion-rc")
                .build();
        when(publicationService.getBySlugOrId("avion-rc")).thenReturn(detail);

        ResponseEntity<PublicationDetailDto> response = controller.getPublicationBySlugOrId("avion-rc");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(detail);
        verify(publicationService).getBySlugOrId("avion-rc");
    }

    @Test
    @DisplayName("recordWhatsAppClick should delegate to publicationService and return 200 OK")
    void recordWhatsAppClick_shouldCallService() {
        WhatsAppClickResponseDto responseDto = WhatsAppClickResponseDto.builder()
                .publicationId(1L)
                .whatsappUrl("https://wa.me/5491100001111")
                .totalClicks(5)
                .build();
        when(publicationService.recordWhatsAppClick(1L)).thenReturn(responseDto);

        ResponseEntity<WhatsAppClickResponseDto> response = controller.recordWhatsAppClick(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDto);
        verify(publicationService).recordWhatsAppClick(1L);
    }

    @Test
    @DisplayName("getCategories should return category tree")
    void getCategories_shouldReturnCategoryTree() {
        List<CategoryTreeDto> tree = List.of(CategoryTreeDto.builder().id(1L).name("Aviones").build());
        when(categoryService.getCategoryTree()).thenReturn(tree);

        ResponseEntity<List<CategoryTreeDto>> response = controller.getCategories();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(categoryService).getCategoryTree();
    }

    @Test
    @DisplayName("getStores should return active stores list")
    void getStores_shouldReturnStores() {
        List<StoreDto> stores = List.of(StoreDto.builder().id(1L).name("HobbyMotors").build());
        when(storeService.getActiveStores()).thenReturn(stores);

        ResponseEntity<List<StoreDto>> response = controller.getStores();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(storeService).getActiveStores();
    }

    @Test
    @DisplayName("getStoreBySlug should return storefront")
    void getStoreBySlug_shouldReturnStorefront() {
        StorefrontDto storefront = StorefrontDto.builder()
                .store(StoreDto.builder().id(1L).slug("hobbymotors").build())
                .publications(List.of())
                .build();
        when(storeService.getStorefrontBySlug("hobbymotors")).thenReturn(storefront);

        ResponseEntity<StorefrontDto> response = controller.getStoreBySlug("hobbymotors");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(storefront);
        verify(storeService).getStorefrontBySlug("hobbymotors");
    }

    @Test
    @DisplayName("getPublicStats should return platform stats")
    void getPublicStats_shouldReturnStats() {
        PublicStatsDto stats = PublicStatsDto.builder().totalPublications(10).totalStores(2).build();
        when(publicationService.getPublicStats()).thenReturn(stats);

        ResponseEntity<PublicStatsDto> response = controller.getPublicStats();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(stats);
        verify(publicationService).getPublicStats();
    }
}
