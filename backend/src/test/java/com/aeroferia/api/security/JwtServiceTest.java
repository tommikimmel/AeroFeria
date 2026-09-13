package com.aeroferia.api.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret",
                "aeroferia_ultra_secure_jwt_token_secret_2026_rc_aeromodelling_key_minimum_256_bits_length!");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L);
    }

    @Test
    @DisplayName("generateToken should produce a valid signed token containing claims")
    void generateToken_shouldProduceValidToken() {
        String token = jwtService.generateToken("piloto@aeroferia.com", 15L, "ROLE_USER", "Piloto RC");

        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token, "piloto@aeroferia.com")).isTrue();
        assertThat(jwtService.isTokenValid(token, "otro@aeroferia.com")).isFalse();

        Claims claims = jwtService.extractAllClaims(token);
        assertThat(claims.getSubject()).isEqualTo("piloto@aeroferia.com");
        assertThat(jwtService.extractEmail(token)).isEqualTo("piloto@aeroferia.com");
        assertThat(jwtService.extractUserId(token)).isEqualTo(15L);
        assertThat(jwtService.extractRole(token)).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("isTokenValid should return false for invalid or malformed tokens")
    void isTokenValid_withMalformedToken_shouldReturnFalse() {
        boolean valid = jwtService.isTokenValid("invalid.jwt.token", "test@aeroferia.com");
        assertThat(valid).isFalse();
    }
}
