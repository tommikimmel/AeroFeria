package com.aeroferia.api.integration;

import com.aeroferia.api.entity.*;
import com.aeroferia.api.entity.enums.Currency;
import com.aeroferia.api.entity.enums.ItemCondition;
import com.aeroferia.api.entity.enums.PublicationStatus;
import com.aeroferia.api.entity.enums.UserRole;
import com.aeroferia.api.repository.CategoryRepository;
import com.aeroferia.api.repository.PublicationRepository;
import com.aeroferia.api.repository.StoreRepository;
import com.aeroferia.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClientBFFIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private Publication testPublication;
    private Publication individualPublication;
    private Store hobbyMotors;
    private Category avionesCategory;

    @BeforeEach
    void setupTestData() {
        publicationRepository.deleteAll();

        hobbyMotors = storeRepository.findBySlug("hobbymotors").orElseThrow();
        avionesCategory = categoryRepository.findBySlug("aviones-y-planeadores").orElseThrow();

        User testUser = userRepository.findByEmail("tester@rc.com").orElseGet(() -> {
            User u = User.builder()
                    .email("tester@rc.com")
                    .passwordHash("hashed")
                    .fullName("Piloto Aeromodelista")
                    .phoneNumber("5491155554444")
                    .locationProvince("Buenos Aires")
                    .locationCity("Morón")
                    .role(UserRole.ROLE_USER)
                    .isActive(true)
                    .build();
            return userRepository.save(u);
        });

        // 1. Aviso de tienda oficial
        testPublication = Publication.builder()
                .title("Radio Spektrum NX8 8 Canales")
                .slug("radio-spektrum-nx8-8-canales")
                .description("Transmisor DSMX nuevo con receptor AR6610T y telemetría completa")
                .condition(ItemCondition.NEW)
                .price(BigDecimal.valueOf(450))
                .currency(Currency.USD)
                .videoUrl("https://youtube.com/watch?v=spektrum")
                .locationProvince("Buenos Aires")
                .locationCity("Vicente López")
                .status(PublicationStatus.ACTIVE)
                .viewsCount(50)
                .whatsappClicksCount(10)
                .category(avionesCategory)
                .store(hobbyMotors)
                .user(hobbyMotors.getUser())
                .images(new ArrayList<>())
                .build();

        PublicationImage img1 = PublicationImage.builder()
                .publication(testPublication)
                .imageUrl("/uploads/spektrum-1.webp")
                .thumbnailUrl("/uploads/spektrum-1-thumb.webp")
                .isCover(true)
                .displayOrder(1)
                .build();
        testPublication.getImages().add(img1);
        testPublication = publicationRepository.save(testPublication);

        // 2. Aviso de usuario particular
        individualPublication = Publication.builder()
                .title("Motor OS 46 AX II Glow Usado")
                .slug("motor-os-46-ax-ii-glow-usado")
                .description("Motor japonés impecable con excelente compresión, solo uso combustible Byron")
                .condition(ItemCondition.USED_GOOD)
                .price(BigDecimal.valueOf(180000))
                .currency(Currency.ARS)
                .locationProvince("Buenos Aires")
                .locationCity("Morón")
                .status(PublicationStatus.ACTIVE)
                .viewsCount(25)
                .whatsappClicksCount(4)
                .category(avionesCategory)
                .store(null)
                .user(testUser)
                .images(new ArrayList<>())
                .build();
        individualPublication = publicationRepository.save(individualPublication);
    }

    @Test
    @DisplayName("INTEGRATION: GET /api/v1/client/categories should return seeded hierarchical taxonomy tree")
    void testGetCategories_Integration() throws Exception {
        mockMvc.perform(get("/api/v1/client/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(6))))
                .andExpect(jsonPath("$[0].slug", is("aviones-y-planeadores")))
                .andExpect(jsonPath("$[0].subcategories", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$[0].subcategories[0].name", is("Entrenadores")));
    }

    @Test
    @DisplayName("INTEGRATION: GET /api/v1/client/stores should return active stores with parsed brands")
    void testGetStores_Integration() throws Exception {
        mockMvc.perform(get("/api/v1/client/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].slug", hasItems("aerojols", "hobbymotors")))
                .andExpect(jsonPath("$[0].isVerified", is(true)))
                .andExpect(jsonPath("$[?(@.slug == 'hobbymotors')].brandsRepresented[*]", hasItem("Futaba")));
    }

    @Test
    @DisplayName("INTEGRATION: GET /api/v1/client/stores/{slug} should return StorefrontDto with store and products")
    void testGetStoreBySlug_Integration() throws Exception {
        mockMvc.perform(get("/api/v1/client/stores/hobbymotors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.store.name", is("HobbyMotors")))
                .andExpect(jsonPath("$.store.isVerified", is(true)))
                .andExpect(jsonPath("$.publications", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.publications[0].title", containsString("Spektrum")));
    }

    @Test
    @DisplayName("INTEGRATION: GET /api/v1/client/stores/inexistente should return 404 NOT_FOUND")
    void testGetStoreBySlug_NotFound_Integration() throws Exception {
        mockMvc.perform(get("/api/v1/client/stores/tienda-fantasma"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Tienda no encontrado")));
    }

    @Test
    @DisplayName("INTEGRATION: GET /api/v1/client/publications should return active catalog with pagination")
    void testGetPublicationsCatalog_Integration() throws Exception {
        mockMvc.perform(get("/api/v1/client/publications?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    @Test
    @DisplayName("INTEGRATION: GET /api/v1/client/publications with faceted filters should filter correctly across classes")
    void testGetPublicationsWithFilters_Integration() throws Exception {
        // Filtrar por término 'Spektrum'
        mockMvc.perform(get("/api/v1/client/publications?search=spektrum"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", containsString("Spektrum")))
                .andExpect(jsonPath("$.content[0].isVerifiedStore", is(true)));

        // Filtrar por sólo tiendas oficiales
        mockMvc.perform(get("/api/v1/client/publications?onlyStores=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].storeName", is("HobbyMotors")));

        // Filtrar por moneda ARS
        mockMvc.perform(get("/api/v1/client/publications?currency=ARS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", containsString("Motor OS")));

        // Filtrar por precio
        mockMvc.perform(get("/api/v1/client/publications?minPrice=400&maxPrice=500&currency=USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", containsString("Spektrum")));
    }

    @Test
    @DisplayName("INTEGRATION: GET /api/v1/client/publications/{slugOrId} should return full detail and increment views")
    void testGetPublicationDetail_Integration() throws Exception {
        int initialViews = testPublication.getViewsCount();

        mockMvc.perform(get("/api/v1/client/publications/" + testPublication.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testPublication.getId().intValue())))
                .andExpect(jsonPath("$.title", is(testPublication.getTitle())))
                .andExpect(jsonPath("$.seller.isStore", is(true)))
                .andExpect(jsonPath("$.seller.storeName", is("HobbyMotors")))
                .andExpect(jsonPath("$.seller.isVerifiedStore", is(true)))
                .andExpect(jsonPath("$.viewsCount", is(initialViews + 1)))
                .andExpect(jsonPath("$.whatsappUrl", containsString("wa.me")));

        // Verificar búsqueda por ID numérico también
        mockMvc.perform(get("/api/v1/client/publications/" + testPublication.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is(testPublication.getSlug())));
    }

    @Test
    @DisplayName("INTEGRATION: POST /api/v1/client/publications/{id}/whatsapp-click should increment clicks atomically and return deep link")
    void testRecordWhatsAppClick_Integration() throws Exception {
        int initialClicks = individualPublication.getWhatsappClicksCount();

        mockMvc.perform(post("/api/v1/client/publications/" + individualPublication.getId() + "/whatsapp-click"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicationId", is(individualPublication.getId().intValue())))
                .andExpect(jsonPath("$.totalClicks", is(initialClicks + 1)))
                .andExpect(jsonPath("$.targetPhoneNumber", is("5491155554444")))
                .andExpect(jsonPath("$.whatsappUrl", startsWith("https://wa.me/5491155554444?text=")))
                .andExpect(jsonPath("$.message", containsString("Motor OS 46 AX II Glow Usado")));
    }

    @Test
    @DisplayName("INTEGRATION: GET /api/v1/client/stats should return live counts")
    void testGetPublicStats_Integration() throws Exception {
        mockMvc.perform(get("/api/v1/client/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPublications", is(2)))
                .andExpect(jsonPath("$.totalStores", greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.totalCategories", greaterThanOrEqualTo(6)));
    }

    @Test
    @DisplayName("INTEGRATION: GET /api/v1/health should return UP status")
    void testHealth_Integration() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }
}
