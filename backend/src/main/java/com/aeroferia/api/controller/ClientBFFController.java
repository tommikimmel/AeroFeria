package com.aeroferia.api.controller;

import com.aeroferia.api.dto.*;
import com.aeroferia.api.entity.enums.Currency;
import com.aeroferia.api.entity.enums.ItemCondition;
import com.aeroferia.api.service.CategoryService;
import com.aeroferia.api.service.PublicationService;
import com.aeroferia.api.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class ClientBFFController {

    private final PublicationService publicationService;
    private final StoreService storeService;
    private final CategoryService categoryService;

    @GetMapping("/publications")
    public ResponseEntity<Page<PublicationCardDto>> getPublications(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) ItemCondition condition,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Currency currency,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Boolean onlyStores,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        CatalogFilterDto filter = CatalogFilterDto.builder()
                .categoryId(categoryId)
                .categorySlug(categorySlug)
                .search(search)
                .province(province)
                .condition(condition)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .currency(currency)
                .storeId(storeId)
                .onlyStores(onlyStores)
                .sort(sort)
                .build();

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50));
        return ResponseEntity.ok(publicationService.getCatalog(filter, pageable));
    }

    @GetMapping("/publications/{slugOrId}")
    public ResponseEntity<PublicationDetailDto> getPublicationBySlugOrId(@PathVariable String slugOrId) {
        return ResponseEntity.ok(publicationService.getBySlugOrId(slugOrId));
    }

    @PostMapping("/publications/{id}/whatsapp-click")
    public ResponseEntity<WhatsAppClickResponseDto> recordWhatsAppClick(@PathVariable Long id) {
        return ResponseEntity.ok(publicationService.recordWhatsAppClick(id));
    }

    @PostMapping("/publications")
    public ResponseEntity<PublicationDetailDto> createPublication(
            @jakarta.validation.Valid @RequestBody CreatePublicationDto dto,
            java.security.Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(publicationService.createPublication(dto, principal.getName()));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryTreeDto>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }

    @GetMapping("/stores")
    public ResponseEntity<List<StoreDto>> getStores() {
        return ResponseEntity.ok(storeService.getActiveStores());
    }

    @GetMapping("/stores/{slug}")
    public ResponseEntity<StorefrontDto> getStoreBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(storeService.getStorefrontBySlug(slug));
    }

    @GetMapping("/stats")
    public ResponseEntity<PublicStatsDto> getPublicStats() {
        return ResponseEntity.ok(publicationService.getPublicStats());
    }
}
