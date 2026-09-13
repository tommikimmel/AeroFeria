package com.aeroferia.api.dto;

import com.aeroferia.api.entity.enums.Currency;
import com.aeroferia.api.entity.enums.ItemCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationCardDto {
    private Long id;
    private String title;
    private String slug;
    private String coverImageUrl;
    private BigDecimal price;
    private Currency currency;
    private ItemCondition condition;
    private String locationProvince;
    private String locationCity;
    private Long categoryId;
    private String categoryName;
    private Long storeId;
    private String storeName;
    private String storeSlug;
    private Boolean isVerifiedStore;
    private Integer viewsCount;
    private LocalDateTime createdAt;
}
