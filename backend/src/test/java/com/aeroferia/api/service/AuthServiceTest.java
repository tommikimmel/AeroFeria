package com.aeroferia.api.service;

import com.aeroferia.api.dto.AuthResponseDto;
import com.aeroferia.api.dto.LoginRequestDto;
import com.aeroferia.api.dto.RegisterRequestDto;
import com.aeroferia.api.entity.Store;
import com.aeroferia.api.entity.User;
import com.aeroferia.api.entity.enums.UserRole;
import com.aeroferia.api.exception.BadRequestException;
import com.aeroferia.api.exception.ResourceNotFoundException;
import com.aeroferia.api.repository.StoreRepository;
import com.aeroferia.api.repository.UserRepository;
import com.aeroferia.api.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("register should succeed when email is not taken")
    void register_shouldSucceed() {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("nuevo@aeroferia.com")
                .password("Password123!")
                .fullName("Juan Aeromodelista")
                .phoneNumber("5491155555555")
                .locationProvince("Buenos Aires")
                .locationCity("Morón")
                .build();

        when(userRepository.existsByEmail("nuevo@aeroferia.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded_hash");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(10L);
            return u;
        });
        when(jwtService.generateToken(anyString(), anyLong(), anyString(), anyString())).thenReturn("mocked_jwt");
        when(storeRepository.findByUserId(10L)).thenReturn(Optional.empty());

        AuthResponseDto response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mocked_jwt");
        assertThat(response.getEmail()).isEqualTo("nuevo@aeroferia.com");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register should throw BadRequestException when email is already in use")
    void register_whenEmailExists_shouldThrow() {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("duplicado@aeroferia.com")
                .password("Password123!")
                .build();

        when(userRepository.existsByEmail("duplicado@aeroferia.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("ya se encuentra registrado");
    }

    @Test
    @DisplayName("login should succeed when credentials are valid")
    void login_shouldSucceed() {
        LoginRequestDto request = LoginRequestDto.builder()
                .email("piloto@aeroferia.com")
                .password("PassCorrecto")
                .build();

        User user = User.builder()
                .id(20L)
                .email("piloto@aeroferia.com")
                .passwordHash("hashed")
                .fullName("Piloto")
                .phoneNumber("5491122223333")
                .role(UserRole.ROLE_USER)
                .isActive(true)
                .build();

        Store store = Store.builder().id(5L).slug("hobbystore").name("Hobby Store").build();

        when(userRepository.findByEmail("piloto@aeroferia.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("PassCorrecto", "hashed")).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyLong(), anyString(), anyString())).thenReturn("valid_token");
        when(storeRepository.findByUserId(20L)).thenReturn(Optional.of(store));

        AuthResponseDto response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("valid_token");
        assertThat(response.getStoreSlug()).isEqualTo("hobbystore");
        assertThat(response.getStoreName()).isEqualTo("Hobby Store");
    }

    @Test
    @DisplayName("login should throw BadCredentialsException when password does not match")
    void login_withWrongPassword_shouldThrow() {
        LoginRequestDto request = LoginRequestDto.builder()
                .email("piloto@aeroferia.com")
                .password("WrongPass")
                .build();

        User user = User.builder()
                .email("piloto@aeroferia.com")
                .passwordHash("hashed")
                .isActive(true)
                .build();

        when(userRepository.findByEmail("piloto@aeroferia.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("login should throw BadRequestException when user is deactivated")
    void login_whenInactive_shouldThrow() {
        LoginRequestDto request = LoginRequestDto.builder()
                .email("inactivo@aeroferia.com")
                .password("Pass")
                .build();

        User user = User.builder()
                .email("inactivo@aeroferia.com")
                .passwordHash("hashed")
                .isActive(false)
                .build();

        when(userRepository.findByEmail("inactivo@aeroferia.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass", "hashed")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("inactiva");
    }

    @Test
    @DisplayName("getUserProfile should return profile data when user exists")
    void getUserProfile_shouldReturnProfile() {
        User user = User.builder()
                .id(30L)
                .email("user@aeroferia.com")
                .fullName("User Name")
                .phoneNumber("123")
                .role(UserRole.ROLE_USER)
                .isActive(true)
                .build();

        when(userRepository.findByEmail("user@aeroferia.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyString(), anyLong(), anyString(), anyString())).thenReturn("tok");
        when(storeRepository.findByUserId(30L)).thenReturn(Optional.empty());

        AuthResponseDto profile = authService.getUserProfile("user@aeroferia.com");
        assertThat(profile.getEmail()).isEqualTo("user@aeroferia.com");
    }

    @Test
    @DisplayName("getUserProfile should throw ResourceNotFoundException when user does not exist")
    void getUserProfile_whenNotFound_shouldThrow() {
        when(userRepository.findByEmail("inexistente@aeroferia.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getUserProfile("inexistente@aeroferia.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
