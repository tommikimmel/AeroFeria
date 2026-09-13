package com.aeroferia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
    private String bannerUrl;
    private String description;
    private List<String> brandsRepresented;
    private Boolean shipsNationwide;
    private String addressLine;
    private String locationProvince;
    private String locationCity;
    private String whatsappNumber;
    private String websiteUrl;
    private String instagramHandle;
    private Boolean isVerified;
    private long activePublicationsCount;
}
