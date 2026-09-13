package com.aeroferia.api.dto;

import com.aeroferia.api.entity.enums.Currency;
import com.aeroferia.api.entity.enums.ItemCondition;
import com.aeroferia.api.entity.enums.PublicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationDetailDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private ItemCondition condition;
    private BigDecimal price;
    private Currency currency;
    private String videoUrl;
    private String locationProvince;
    private String locationCity;
    private PublicationStatus status;
    private Integer viewsCount;
    private Integer whatsappClicksCount;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private List<PublicationImageDto> images;
    private SellerInfoDto seller;
    private String whatsappUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
