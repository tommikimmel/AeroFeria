package com.aeroferia.api.controller;

import com.aeroferia.api.dto.AuthResponseDto;
import com.aeroferia.api.dto.LoginRequestDto;
import com.aeroferia.api.dto.RegisterRequestDto;
import com.aeroferia.api.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("register should return 201 CREATED with AuthResponseDto")
    void register_shouldReturnCreated() {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("test@aeroferia.com")
                .password("Pass123!")
                .fullName("Test User")
                .phoneNumber("123456")
                .build();

        AuthResponseDto authResponse = AuthResponseDto.builder()
                .token("tok")
                .email("test@aeroferia.com")
                .build();

        when(authService.register(any(RegisterRequestDto.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponseDto> response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(authResponse);
        verify(authService).register(request);
    }

    @Test
    @DisplayName("login should return 200 OK with AuthResponseDto")
    void login_shouldReturnOk() {
        LoginRequestDto request = LoginRequestDto.builder()
                .email("test@aeroferia.com")
                .password("Pass123!")
                .build();

        AuthResponseDto authResponse = AuthResponseDto.builder()
                .token("tok")
                .email("test@aeroferia.com")
                .build();

        when(authService.login(any(LoginRequestDto.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponseDto> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(authResponse);
        verify(authService).login(request);
    }

    @Test
    @DisplayName("me should return 200 OK when principal is present")
    void me_withPrincipal_shouldReturnOk() {
        Principal principal = () -> "test@aeroferia.com";
        AuthResponseDto profile = AuthResponseDto.builder().email("test@aeroferia.com").build();

        when(authService.getUserProfile("test@aeroferia.com")).thenReturn(profile);

        ResponseEntity<AuthResponseDto> response = authController.me(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(profile);
    }

    @Test
    @DisplayName("me should return 401 UNAUTHORIZED when principal is null")
    void me_withoutPrincipal_shouldReturnUnauthorized() {
        ResponseEntity<AuthResponseDto> response = authController.me(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
