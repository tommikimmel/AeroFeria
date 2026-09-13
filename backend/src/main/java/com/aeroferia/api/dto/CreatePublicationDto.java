package com.aeroferia.api.dto;

import com.aeroferia.api.entity.enums.Currency;
import com.aeroferia.api.entity.enums.ItemCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePublicationDto {

    @NotBlank(message = "El título de la publicación es obligatorio")
    @Size(max = 100, message = "El título no puede superar los 100 caracteres")
    private String title;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @NotNull(message = "La condición del artículo es obligatoria")
    private ItemCondition condition;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    private BigDecimal price;

    @NotNull(message = "La moneda es obligatoria (USD o ARS)")
    private Currency currency;

    private String locationProvince;
    private String locationCity;
    private String videoUrl;

    @NotBlank(message = "La descripción técnica es obligatoria")
    private String description;

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();
}
