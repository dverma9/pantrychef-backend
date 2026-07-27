package com.pantrychef.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDto {

    private Long id;

    @NotBlank(message = "Ingredient name is required")
    private String name;

    private String quantity;
    private String unit;
    private LocalDateTime createdAt;
}