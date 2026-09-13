package com.aeroferia.api.entity;

import com.aeroferia.api.dto.CatalogFilterDto;
import com.aeroferia.api.dto.PublicStatsDto;
import com.aeroferia.api.entity.enums.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class EntityAndEnumTest {

    @Test
    @DisplayName("Test Enums values and valueOf")
    void testEnums() {
        assertThat(Currency.values()).contains(Currency.ARS, Currency.USD);
        assertThat(Currency.valueOf("ARS")).isEqualTo(Currency.ARS);

        assertThat(ItemCondition.values()).contains(ItemCondition.NEW, ItemCondition.LIKE_NEW, ItemCondition.USED_GOOD, ItemCondition.FOR_PARTS);
        assertThat(ItemCondition.valueOf("NEW")).isEqualTo(ItemCondition.NEW);

        assertThat(ModerationAction.values()).contains(ModerationAction.APPROVED, ModerationAction.REJECTED, ModerationAction.FORCE_DELETED);
        assertThat(ModerationAction.valueOf("APPROVED")).isEqualTo(ModerationAction.APPROVED);

        assertThat(PublicationStatus.values()).contains(PublicationStatus.PENDING, PublicationStatus.ACTIVE, PublicationStatus.SOLD);
        assertThat(PublicationStatus.valueOf("ACTIVE")).isEqualTo(PublicationStatus.ACTIVE);

        assertThat(UserRole.values()).contains(UserRole.ROLE_USER, UserRole.ROLE_ADMIN);
        assertThat(UserRole.valueOf("ROLE_ADMIN")).isEqualTo(UserRole.ROLE_ADMIN);
    }

    @Test
    @DisplayName("Test ModerationLog entity builder, getters and setters")
    void testModerationLog() {
        User admin = User.builder().id(1L).fullName("Admin").build();
        Publication pub = Publication.builder().id(10L).build();
        LocalDateTime now = LocalDateTime.now();

        ModerationLog log = ModerationLog.builder()
                .id(100L)
                .admin(admin)
                .publication(pub)
                .action(ModerationAction.APPROVED)
                .notes("Cumple las normas")
                .createdAt(now)
                .build();

        assertThat(log.getId()).isEqualTo(100L);
        assertThat(log.getAdmin()).isEqualTo(admin);
        assertThat(log.getPublication()).isEqualTo(pub);
        assertThat(log.getAction()).isEqualTo(ModerationAction.APPROVED);
        assertThat(log.getNotes()).isEqualTo("Cumple las normas");
        assertThat(log.getCreatedAt()).isEqualTo(now);

        log.setAction(ModerationAction.REJECTED);
        log.setNotes("Inapropiado");
        assertThat(log.getAction()).isEqualTo(ModerationAction.REJECTED);
        assertThat(log.getNotes()).isEqualTo("Inapropiado");
    }

    @Test
    @DisplayName("Test User entity builder, getters and setters")
    void testUser() {
        User user = new User();
        user.setId(5L);
        user.setEmail("test@rc.com");
        user.setPasswordHash("pass");
        user.setFullName("Juan Perez");
        user.setAvatarUrl("/uploads/avatar.webp");
        user.setPhoneNumber("5491112345678");
        user.setLocationProvince("Buenos Aires");
        user.setLocationCity("San Martín");
        user.setRole(UserRole.ROLE_USER);
        user.setIsActive(true);
        user.setStore(null);
        user.setPublications(new ArrayList<>());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        assertThat(user.getId()).isEqualTo(5L);
        assertThat(user.getEmail()).isEqualTo("test@rc.com");
        assertThat(user.getFullName()).isEqualTo("Juan Perez");
        assertThat(user.getAvatarUrl()).isEqualTo("/uploads/avatar.webp");
        assertThat(user.getPhoneNumber()).isEqualTo("5491112345678");
        assertThat(user.getLocationProvince()).isEqualTo("Buenos Aires");
        assertThat(user.getLocationCity()).isEqualTo("San Martín");
        assertThat(user.getRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(user.getIsActive()).isTrue();
        assertThat(user.getStore()).isNull();
        assertThat(user.getPublications()).isEmpty();
    }

    @Test
    @DisplayName("Test Store entity builder, getters and setters")
    void testStore() {
        Store store = new Store();
        store.setId(3L);
        store.setName("AeroJols");
        store.setSlug("aerojols");
        store.setLogoUrl("/logo.webp");
        store.setBannerUrl("/banner.webp");
        store.setDescription("Venta de modelos");
        store.setBrandsRepresented("FrSky, DLE");
        store.setShipsNationwide(true);
        store.setAddressLine("Calle 12");
        store.setLocationProvince("Buenos Aires");
        store.setLocationCity("La Plata");
        store.setWhatsappNumber("5492211234567");
        store.setWebsiteUrl("https://aerojols.com");
        store.setInstagramHandle("@aerojols");
        store.setIsVerified(true);
        store.setIsActive(true);
        store.setPublications(new ArrayList<>());
        store.setCreatedAt(LocalDateTime.now());
        store.setUpdatedAt(LocalDateTime.now());

        assertThat(store.getId()).isEqualTo(3L);
        assertThat(store.getName()).isEqualTo("AeroJols");
        assertThat(store.getSlug()).isEqualTo("aerojols");
        assertThat(store.getLogoUrl()).isEqualTo("/logo.webp");
        assertThat(store.getBannerUrl()).isEqualTo("/banner.webp");
        assertThat(store.getDescription()).isEqualTo("Venta de modelos");
        assertThat(store.getBrandsRepresented()).isEqualTo("FrSky, DLE");
        assertThat(store.getShipsNationwide()).isTrue();
        assertThat(store.getAddressLine()).isEqualTo("Calle 12");
        assertThat(store.getLocationProvince()).isEqualTo("Buenos Aires");
        assertThat(store.getLocationCity()).isEqualTo("La Plata");
        assertThat(store.getWhatsappNumber()).isEqualTo("5492211234567");
        assertThat(store.getWebsiteUrl()).isEqualTo("https://aerojols.com");
        assertThat(store.getInstagramHandle()).isEqualTo("@aerojols");
        assertThat(store.getIsVerified()).isTrue();
        assertThat(store.getIsActive()).isTrue();
        assertThat(store.getPublications()).isEmpty();
    }

    @Test
    @DisplayName("Test Publication entity full getters and setters")
    void testPublication() {
        User user = User.builder().id(1L).build();
        Category cat = Category.builder().id(2L).build();
        User admin = User.builder().id(99L).build();
        LocalDateTime now = LocalDateTime.now();

        Publication p = new Publication();
        p.setId(50L);
        p.setUser(user);
        p.setStore(null);
        p.setCategory(cat);
        p.setTitle("Avión Acrobático");
        p.setSlug("avion-acrobatico");
        p.setDescription("Modelo 3D con motor DLE 35");
        p.setCondition(ItemCondition.LIKE_NEW);
        p.setPrice(BigDecimal.valueOf(1500));
        p.setCurrency(Currency.USD);
        p.setVideoUrl("https://youtube.com/watch?v=fly");
        p.setLocationProvince("Santa Fe");
        p.setLocationCity("Rosario");
        p.setStatus(PublicationStatus.ACTIVE);
        p.setRejectionReason(null);
        p.setViewsCount(42);
        p.setWhatsappClicksCount(8);
        p.setImages(new ArrayList<>());
        p.setCreatedAt(now);
        p.setUpdatedAt(now);
        p.setModeratedAt(now);
        p.setModeratedBy(admin);

        assertThat(p.getId()).isEqualTo(50L);
        assertThat(p.getUser()).isEqualTo(user);
        assertThat(p.getCategory()).isEqualTo(cat);
        assertThat(p.getTitle()).isEqualTo("Avión Acrobático");
        assertThat(p.getSlug()).isEqualTo("avion-acrobatico");
        assertThat(p.getDescription()).isEqualTo("Modelo 3D con motor DLE 35");
        assertThat(p.getCondition()).isEqualTo(ItemCondition.LIKE_NEW);
        assertThat(p.getPrice()).isEqualTo(BigDecimal.valueOf(1500));
        assertThat(p.getCurrency()).isEqualTo(Currency.USD);
        assertThat(p.getVideoUrl()).isEqualTo("https://youtube.com/watch?v=fly");
        assertThat(p.getLocationProvince()).isEqualTo("Santa Fe");
        assertThat(p.getLocationCity()).isEqualTo("Rosario");
        assertThat(p.getStatus()).isEqualTo(PublicationStatus.ACTIVE);
        assertThat(p.getViewsCount()).isEqualTo(42);
        assertThat(p.getWhatsappClicksCount()).isEqualTo(8);
        assertThat(p.getCreatedAt()).isEqualTo(now);
        assertThat(p.getUpdatedAt()).isEqualTo(now);
        assertThat(p.getModeratedAt()).isEqualTo(now);
        assertThat(p.getModeratedBy()).isEqualTo(admin);

        p.setRejectionReason("Falta precio claro");
        assertThat(p.getRejectionReason()).isEqualTo("Falta precio claro");
    }

    @Test
    @DisplayName("Test PublicationImage entity getters and setters")
    void testPublicationImage() {
        PublicationImage img = new PublicationImage();
        img.setId(1L);
        img.setImageUrl("/img.webp");
        img.setThumbnailUrl("/thumb.webp");
        img.setIsCover(true);
        img.setDisplayOrder(0);
        img.setCreatedAt(LocalDateTime.now());

        assertThat(img.getId()).isEqualTo(1L);
        assertThat(img.getImageUrl()).isEqualTo("/img.webp");
        assertThat(img.getThumbnailUrl()).isEqualTo("/thumb.webp");
        assertThat(img.getIsCover()).isTrue();
        assertThat(img.getDisplayOrder()).isEqualTo(0);
        assertThat(img.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Test DTOs getters and setters")
    void testDtos() {
        CatalogFilterDto filter = new CatalogFilterDto();
        filter.setCategorySlug("radios");
        assertThat(filter.getCategorySlug()).isEqualTo("radios");

        PublicStatsDto stats = new PublicStatsDto(10, 2, 6, 50);
        assertThat(stats.getTotalPublications()).isEqualTo(10);
        assertThat(stats.getTotalStores()).isEqualTo(2);
        assertThat(stats.getTotalCategories()).isEqualTo(6);
        assertThat(stats.getTotalUsers()).isEqualTo(50);
    }

    @Test
    @DisplayName("Test constructors and utility instances")
    void testConstructors() {
        com.aeroferia.api.exception.ResourceNotFoundException ex = 
                new com.aeroferia.api.exception.ResourceNotFoundException("Mensaje directo");
        assertThat(ex.getMessage()).isEqualTo("Mensaje directo");

        com.aeroferia.api.specification.PublicationSpecification specInstance = 
                new com.aeroferia.api.specification.PublicationSpecification();
        assertThat(specInstance).isNotNull();

        com.aeroferia.api.AeroFeriaApplication app = new com.aeroferia.api.AeroFeriaApplication();
        assertThat(app).isNotNull();
    }
}
