package com.aeroferia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppClickResponseDto {
    private Long publicationId;
    private String whatsappUrl;
    private Integer totalClicks;
    private String targetPhoneNumber;
    private String message;
}
