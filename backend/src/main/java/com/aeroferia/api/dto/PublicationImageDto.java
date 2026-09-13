package com.aeroferia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationImageDto {
    private Long id;
    private String imageUrl;
    private String thumbnailUrl;
    private Boolean isCover;
    private Integer displayOrder;
}
