package com.aeroferia.api.controller;

import com.aeroferia.api.entity.Category;
import com.aeroferia.api.entity.Store;
import com.aeroferia.api.repository.CategoryRepository;
import com.aeroferia.api.repository.PublicationRepository;
import com.aeroferia.api.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class ClientBFFController {

    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final PublicationRepository publicationRepository;

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryRepository.findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAsc());
    }

    @GetMapping("/stores")
    public ResponseEntity<List<Store>> getStores() {
        return ResponseEntity.ok(storeRepository.findByIsActiveTrueOrderByIsVerifiedDescNameAsc());
    }

    @GetMapping("/stores/{slug}")
    public ResponseEntity<?> getStoreBySlug(@PathVariable String slug) {
        return storeRepository.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPublicStats() {
        return ResponseEntity.ok(Map.of(
                "totalStores", storeRepository.count(),
                "totalCategories", categoryRepository.count()
        ));
    }
}
