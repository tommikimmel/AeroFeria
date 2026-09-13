package com.aeroferia.api.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminBFFSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("SECURITY INTEGRATION: Anonymous user accessing /api/v1/admin/stats should be blocked")
    void testAnonymousAccessToAdmin_shouldBeBlocked() throws Exception {
        mockMvc.perform(get("/api/v1/admin/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("SECURITY INTEGRATION: Regular user with ROLE_USER should receive 403 FORBIDDEN")
    void testRegularUserAccessToAdmin_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("SECURITY INTEGRATION: Admin with ROLE_ADMIN should successfully access /api/v1/admin/stats")
    void testAdminUserAccessToAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/v1/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").exists())
                .andExpect(jsonPath("$.totalStores").exists());
    }
}
