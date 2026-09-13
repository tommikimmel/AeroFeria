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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto dto) {
        String cleanEmail = dto.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new BadRequestException("El correo electrónico ya se encuentra registrado");
        }

        User user = User.builder()
                .email(cleanEmail)
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName().trim())
                .phoneNumber(dto.getPhoneNumber().trim())
                .locationProvince(dto.getLocationProvince() != null ? dto.getLocationProvince().trim() : null)
                .locationCity(dto.getLocationCity() != null ? dto.getLocationCity().trim() : null)
                .role(UserRole.ROLE_USER)
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("Nuevo usuario registrado: {} (ID: {})", user.getEmail(), user.getId());

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto dto) {
        String cleanEmail = dto.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BadRequestException("La cuenta de usuario se encuentra inactiva");
        }

        log.info("Inicio de sesión exitoso: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        return buildAuthResponse(user);
    }

    private AuthResponseDto buildAuthResponse(User user) {
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().name(),
                user.getFullName()
        );

        Store store = storeRepository.findByUserId(user.getId()).orElse(null);

        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .locationProvince(user.getLocationProvince())
                .locationCity(user.getLocationCity())
                .role(user.getRole().name())
                .storeSlug(store != null ? store.getSlug() : null)
                .storeName(store != null ? store.getName() : null)
                .build();
    }
}
