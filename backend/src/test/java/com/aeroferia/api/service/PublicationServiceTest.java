package com.aeroferia.api.service;

import com.aeroferia.api.dto.*;
import com.aeroferia.api.entity.*;
import com.aeroferia.api.entity.enums.Currency;
import com.aeroferia.api.entity.enums.ItemCondition;
import com.aeroferia.api.entity.enums.PublicationStatus;
import com.aeroferia.api.exception.ResourceNotFoundException;
import com.aeroferia.api.repository.CategoryRepository;
import com.aeroferia.api.repository.PublicationRepository;
import com.aeroferia.api.repository.StoreRepository;
import com.aeroferia.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicationServiceTest {

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PublicationService publicationService;

    private User user;
    private Store store;
    private Category category;
    private Publication publication;
    private Publication individualPub;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullName("Carlos Aeromodelista")
                .email("carlos@rc.com")
                .phoneNumber("+54 9 11 9999-8888")
                .locationProvince("Buenos Aires")
                .locationCity("San Isidro")
                .build();

        store = Store.builder()
                .id(2L)
                .name("AeroShop")
                .slug("aeroshop")
                .logoUrl("/uploads/stores/aeroshop.webp")
                .whatsappNumber("+54 9 11 2222-3333")
                .isVerified(true)
                .build();

        category = Category.builder()
                .id(3L)
                .name("Turbinas")
                .slug("turbinas")
                .build();

        PublicationImage img1 = PublicationImage.builder()
                .id(10L)
                .imageUrl("/uploads/img1.webp")
                .isCover(false)
                .build();

        PublicationImage img2 = PublicationImage.builder()
                .id(11L)
                .imageUrl("/uploads/img2.webp")
                .isCover(true)
                .build();

        publication = Publication.builder()
                .id(100L)
                .title("Turbina KingTech K85G4+")
                .slug("turbina-kingtech-k85g4")
                .description("Turbina nueva en caja sin rodaje")
                .condition(ItemCondition.NEW)
                .price(BigDecimal.valueOf(2300))
                .currency(Currency.USD)
                .videoUrl("https://youtube.com/watch?v=12345")
                .locationProvince("Buenos Aires")
                .locationCity("San Isidro")
                .status(PublicationStatus.ACTIVE)
                .viewsCount(10)
                .whatsappClicksCount(2)
                .category(category)
                .user(user)
                .store(store)
                .images(new ArrayList<>(List.of(img1, img2)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        individualPub = Publication.builder()
                .id(101L)
                .title("Hélice Mejzlik 24x10")
                .slug("helice-mejzlik-24x10")
                .description("Hélice carbono para motor 50cc")
                .condition(ItemCondition.USED_GOOD)
                .price(BigDecimal.valueOf(95000))
                .currency(Currency.ARS)
                .locationProvince("Córdoba")
                .locationCity("Córdoba Capital")
                .status(PublicationStatus.ACTIVE)
                .viewsCount(5)
                .whatsappClicksCount(1)
                .category(category)
                .user(user)
                .store(null)
                .images(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getCatalog should return paginated publication cards with various sort options")
    void getCatalog_shouldReturnPageOfCards() {
        when(publicationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(publication, individualPub)));

        // Test with price_asc sort
        CatalogFilterDto filter1 = CatalogFilterDto.builder().sort("price_asc").build();
        Page<PublicationCardDto> page1 = publicationService.getCatalog(filter1, PageRequest.of(0, 10));
        assertThat(page1.getContent()).hasSize(2);

        // Test with price_desc sort
        CatalogFilterDto filter2 = CatalogFilterDto.builder().sort("price_desc").build();
        Page<PublicationCardDto> page2 = publicationService.getCatalog(filter2, PageRequest.of(0, 10));
        assertThat(page2.getContent()).hasSize(2);

        // Test with popular sort
        CatalogFilterDto filter3 = CatalogFilterDto.builder().sort("popular").build();
        Page<PublicationCardDto> page3 = publicationService.getCatalog(filter3, PageRequest.of(0, 10));
        assertThat(page3.getContent()).hasSize(2);

        // Test with null filter and default sort
        Page<PublicationCardDto> page4 = publicationService.getCatalog(null, PageRequest.of(0, 10));
        assertThat(page4.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("getBySlugOrId should find publication by numeric ID and increment views")
    void getBySlugOrId_withNumericId_shouldReturnDetailAndIncrementViews() {
        when(publicationRepository.findByIdAndStatus(100L, PublicationStatus.ACTIVE))
                .thenReturn(Optional.of(publication));

        PublicationDetailDto detail = publicationService.getBySlugOrId("100");

        assertThat(detail).isNotNull();
        assertThat(detail.getId()).isEqualTo(100L);
        assertThat(detail.getTitle()).isEqualTo("Turbina KingTech K85G4+");
        assertThat(detail.getViewsCount()).isEqualTo(11);
        assertThat(detail.getSeller().getIsStore()).isTrue();
        assertThat(detail.getSeller().getStoreName()).isEqualTo("AeroShop");
        assertThat(detail.getWhatsappUrl()).contains("5491122223333");

        verify(publicationRepository).incrementViewsCount(100L);
    }

    @Test
    @DisplayName("getBySlugOrId should find publication by slug string and increment views")
    void getBySlugOrId_withSlug_shouldReturnDetail() {
        when(publicationRepository.findBySlugAndStatus("turbina-kingtech-k85g4", PublicationStatus.ACTIVE))
                .thenReturn(Optional.of(publication));

        PublicationDetailDto detail = publicationService.getBySlugOrId("turbina-kingtech-k85g4");

        assertThat(detail).isNotNull();
        assertThat(detail.getSlug()).isEqualTo("turbina-kingtech-k85g4");
        verify(publicationRepository).incrementViewsCount(100L);
    }

    @Test
    @DisplayName("getBySlugOrId should throw ResourceNotFoundException when publication not found by ID")
    void getBySlugOrId_notFoundById_shouldThrow() {
        when(publicationRepository.findByIdAndStatus(999L, PublicationStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicationService.getBySlugOrId("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Publicación no encontrado");
    }

    @Test
    @DisplayName("getBySlugOrId should throw ResourceNotFoundException when publication not found by slug")
    void getBySlugOrId_notFoundBySlug_shouldThrow() {
        when(publicationRepository.findBySlugAndStatus("inexistente", PublicationStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicationService.getBySlugOrId("inexistente"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Publicación no encontrado");
    }

    @Test
    @DisplayName("recordWhatsAppClick should increment clicks and generate deep link with store phone")
    void recordWhatsAppClick_withStorePhone_shouldIncrementAndReturnDto() {
        when(publicationRepository.findByIdAndStatus(100L, PublicationStatus.ACTIVE))
                .thenReturn(Optional.of(publication));

        WhatsAppClickResponseDto response = publicationService.recordWhatsAppClick(100L);

        assertThat(response).isNotNull();
        assertThat(response.getPublicationId()).isEqualTo(100L);
        assertThat(response.getTotalClicks()).isEqualTo(3);
        assertThat(response.getTargetPhoneNumber()).isEqualTo("5491122223333");
        assertThat(response.getWhatsappUrl()).startsWith("https://wa.me/5491122223333?text=");
        assertThat(response.getMessage()).contains("Turbina KingTech K85G4+");

        verify(publicationRepository).incrementWhatsappClicksCount(100L);
    }

    @Test
    @DisplayName("recordWhatsAppClick should use user phone when publication has no store")
    void recordWhatsAppClick_withUserPhone_shouldIncrementAndReturnDto() {
        when(publicationRepository.findByIdAndStatus(101L, PublicationStatus.ACTIVE))
                .thenReturn(Optional.of(individualPub));

        WhatsAppClickResponseDto response = publicationService.recordWhatsAppClick(101L);

        assertThat(response).isNotNull();
        assertThat(response.getTargetPhoneNumber()).isEqualTo("5491199998888");
        assertThat(response.getWhatsappUrl()).startsWith("https://wa.me/5491199998888?text=");
    }

    @Test
    @DisplayName("recordWhatsAppClick should throw ResourceNotFoundException when publication not found")
    void recordWhatsAppClick_notFound_shouldThrow() {
        when(publicationRepository.findByIdAndStatus(888L, PublicationStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicationService.recordWhatsAppClick(888L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getPublicStats should aggregate counts from all repositories")
    void getPublicStats_shouldReturnMetrics() {
        when(publicationRepository.countByStatus(PublicationStatus.ACTIVE)).thenReturn(42L);
        when(storeRepository.count()).thenReturn(5L);
        when(categoryRepository.count()).thenReturn(18L);
        when(userRepository.count()).thenReturn(150L);

        PublicStatsDto stats = publicationService.getPublicStats();

        assertThat(stats.getTotalPublications()).isEqualTo(42L);
        assertThat(stats.getTotalStores()).isEqualTo(5L);
        assertThat(stats.getTotalCategories()).isEqualTo(18L);
        assertThat(stats.getTotalUsers()).isEqualTo(150L);
    }

    @Test
    @DisplayName("mapToCardDto should fallback to first image if no image has isCover=true")
    void mapToCardDto_noCoverImage_shouldFallbackToFirst() {
        PublicationImage imgOnly = PublicationImage.builder()
                .id(20L)
                .imageUrl("/uploads/first.webp")
                .isCover(false)
                .build();

        individualPub.setImages(List.of(imgOnly));

        PublicationCardDto card = publicationService.mapToCardDto(individualPub);

        assertThat(card.getCoverImageUrl()).isEqualTo("/uploads/first.webp");
        assertThat(card.getIsVerifiedStore()).isFalse();
        assertThat(card.getStoreName()).isNull();
    }

    @Test
    @DisplayName("createPublication should create and persist publication with images and return detail")
    void createPublication_shouldCreateAndReturnDetail() {
        CreatePublicationDto dto = CreatePublicationDto.builder()
                .title("Radio Futaba T10J")
                .categoryId(3L)
                .condition(ItemCondition.NEW)
                .price(new BigDecimal("450.00"))
                .currency(Currency.USD)
                .locationProvince("Córdoba")
                .locationCity("Villa Carlos Paz")
                .description("Excelente radio para aeromodelos")
                .imageUrls(List.of("https://img.com/1.jpg", "https://img.com/2.jpg"))
                .build();

        when(userRepository.findByEmail("carlos@rc.com")).thenReturn(Optional.of(user));
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category));
        when(storeRepository.findByUserId(1L)).thenReturn(Optional.of(store));
        when(publicationRepository.save(any(Publication.class))).thenAnswer(i -> {
            Publication p = i.getArgument(0);
            p.setId(99L);
            return p;
        });

        PublicationDetailDto detail = publicationService.createPublication(dto, "carlos@rc.com");

        assertThat(detail).isNotNull();
        assertThat(detail.getTitle()).isEqualTo("Radio Futaba T10J");
        assertThat(detail.getImages()).hasSize(2);
        assertThat(detail.getImages().getFirst().getIsCover()).isTrue();
        verify(publicationRepository).save(any(Publication.class));
    }

    @Test
    @DisplayName("createPublication should throw ResourceNotFoundException when user or category not found")
    void createPublication_whenUserNotFound_shouldThrow() {
        CreatePublicationDto dto = CreatePublicationDto.builder().build();
        when(userRepository.findByEmail("inexistente@rc.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicationService.createPublication(dto, "inexistente@rc.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
