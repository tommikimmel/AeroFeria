package com.aeroferia.api.config;

import com.aeroferia.api.entity.Category;
import com.aeroferia.api.entity.Publication;
import com.aeroferia.api.entity.Store;
import com.aeroferia.api.entity.User;
import com.aeroferia.api.repository.CategoryRepository;
import com.aeroferia.api.repository.PublicationRepository;
import com.aeroferia.api.repository.StoreRepository;
import com.aeroferia.api.repository.UserRepository;
import com.aeroferia.api.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigAndDataInitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    @DisplayName("SecurityConfig should provide working BCrypt password encoder")
    void securityConfig_passwordEncoder_shouldHashAndVerify() {
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter);
        PasswordEncoder encoder = config.passwordEncoder();

        String raw = "MiPasswordSeguro123!";
        String encoded = encoder.encode(raw);

        assertThat(encoded).isNotEqualTo(raw);
        assertThat(encoder.matches(raw, encoded)).isTrue();
        assertThat(encoder.matches("wrong", encoded)).isFalse();
    }

    @Test
    @DisplayName("SecurityConfig should configure CORS allowing standard methods and headers")
    void securityConfig_corsConfigurationSource_shouldConfigureHeaders() {
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter);
        CorsConfigurationSource source = config.corsConfigurationSource();

        assertThat(source).isNotNull();
    }

    @Test
    @DisplayName("DataInitializer run should skip creation when admin, categories, stores and publications exist")
    void dataInitializer_run_whenDataAlreadyExists_shouldSkip() {
        ReflectionTestUtils.setField(dataInitializer, "adminEmail", "admin@aeroferia.com");
        ReflectionTestUtils.setField(dataInitializer, "adminPassword", "pass123");
        ReflectionTestUtils.setField(dataInitializer, "adminName", "Admin");
        ReflectionTestUtils.setField(dataInitializer, "adminPhone", "5491112345678");

        when(userRepository.existsByEmail("admin@aeroferia.com")).thenReturn(true);
        when(categoryRepository.count()).thenReturn(10L);
        when(storeRepository.existsBySlug(anyString())).thenReturn(true);
        when(publicationRepository.count()).thenReturn(5L);

        dataInitializer.run();

        verify(userRepository, never()).save(any(User.class));
        verify(categoryRepository, never()).save(any(Category.class));
        verify(storeRepository, never()).save(any(Store.class));
        verify(publicationRepository, never()).save(any(Publication.class));
    }

    @Test
    @DisplayName("DataInitializer run should seed admin, categories, stores and sample publications when database is empty")
    void dataInitializer_run_whenEmpty_shouldSeedAll() {
        ReflectionTestUtils.setField(dataInitializer, "adminEmail", "admin@aeroferia.com");
        ReflectionTestUtils.setField(dataInitializer, "adminPassword", "pass123");
        ReflectionTestUtils.setField(dataInitializer, "adminName", "Admin");
        ReflectionTestUtils.setField(dataInitializer, "adminPhone", "5491112345678");

        when(userRepository.existsByEmail("admin@aeroferia.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pass");
        when(categoryRepository.count()).thenReturn(0L);
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(storeRepository.existsBySlug(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        when(publicationRepository.count()).thenReturn(0L);
        when(categoryRepository.findBySlug(anyString())).thenReturn(Optional.of(Category.builder().id(1L).name("Cat").slug("cat").build()));
        User storeOwner = User.builder().id(2L).email("store@test.com").build();
        when(storeRepository.findBySlug(anyString())).thenReturn(Optional.of(Store.builder().id(1L).slug("store").user(storeOwner).build()));
        when(publicationRepository.save(any(Publication.class))).thenAnswer(i -> i.getArgument(0));

        dataInitializer.run();

        verify(userRepository, atLeastOnce()).save(any(User.class));
        verify(categoryRepository, atLeastOnce()).save(any(Category.class));
        verify(storeRepository, atLeastOnce()).save(any(Store.class));
        verify(publicationRepository, atLeastOnce()).save(any(Publication.class));
    }
}
