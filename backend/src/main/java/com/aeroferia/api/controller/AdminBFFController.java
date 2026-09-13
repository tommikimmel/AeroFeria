package com.aeroferia.api.controller;

import com.aeroferia.api.entity.enums.PublicationStatus;
import com.aeroferia.api.repository.CategoryRepository;
import com.aeroferia.api.repository.PublicationRepository;
import com.aeroferia.api.repository.StoreRepository;
import com.aeroferia.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminBFFController {

    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        return ResponseEntity.ok(Map.of(
                "totalUsers", userRepository.count(),
                "activePublications", publicationRepository.countByStatus(PublicationStatus.ACTIVE),
                "pendingPublications", publicationRepository.countByStatus(PublicationStatus.PENDING),
                "totalStores", storeRepository.count(),
                "totalCategories", categoryRepository.count()
        ));
    }
}
