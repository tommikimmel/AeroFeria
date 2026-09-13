package com.aeroferia.api.dto;

import com.aeroferia.api.entity.enums.Currency;
import com.aeroferia.api.entity.enums.ItemCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogFilterDto {
    private Long categoryId;
    private String categorySlug;
    private String search;
    private String province;
    private ItemCondition condition;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Currency currency;
    private Long storeId;
    private Boolean onlyStores;
    private String sort; // "recent", "price_asc", "price_desc", "popular"
}
