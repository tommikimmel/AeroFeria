package com.aeroferia.api.controller;

import com.aeroferia.api.entity.enums.PublicationStatus;
import com.aeroferia.api.repository.CategoryRepository;
import com.aeroferia.api.repository.PublicationRepository;
import com.aeroferia.api.repository.StoreRepository;
import com.aeroferia.api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBFFControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AdminBFFController controller;

    @Test
    @DisplayName("getAdminStats should return admin statistics map")
    void getAdminStats_shouldReturnMetrics() {
        when(userRepository.count()).thenReturn(100L);
        when(publicationRepository.countByStatus(PublicationStatus.ACTIVE)).thenReturn(40L);
        when(publicationRepository.countByStatus(PublicationStatus.PENDING)).thenReturn(5L);
        when(storeRepository.count()).thenReturn(3L);
        when(categoryRepository.count()).thenReturn(12L);

        ResponseEntity<Map<String, Object>> response = controller.getAdminStats();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("totalUsers")).isEqualTo(100L);
        assertThat(response.getBody().get("activePublications")).isEqualTo(40L);
        assertThat(response.getBody().get("pendingPublications")).isEqualTo(5L);
        assertThat(response.getBody().get("totalStores")).isEqualTo(3L);
        assertThat(response.getBody().get("totalCategories")).isEqualTo(12L);
    }
}
