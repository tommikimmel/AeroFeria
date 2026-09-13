package com.aeroferia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String locationProvince;
    private String locationCity;
    private String role;
    private String storeSlug;
    private String storeName;
}
