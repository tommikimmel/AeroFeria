package com.aeroferia.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HealthControllerTest {

    private final HealthController healthController = new HealthController();

    @Test
    @DisplayName("checkHealth should return UP status and application metadata")
    void checkHealth_shouldReturnOkAndStatusUp() {
        ResponseEntity<Map<String, Object>> response = healthController.checkHealth();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
        assertThat(response.getBody().get("app")).isEqualTo("AeroFeria API");
    }
}
