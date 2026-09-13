package com.aeroferia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerInfoDto {
    private Long id;
    private String fullName;
    private String avatarUrl;
    private String phoneNumber;
    private String locationProvince;
    private String locationCity;
    private Boolean isStore;
    private Long storeId;
    private String storeName;
    private String storeSlug;
    private String storeLogoUrl;
    private Boolean isVerifiedStore;
}
