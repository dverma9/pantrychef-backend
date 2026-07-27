package com.pantrychef.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenceDto {

    private Long id;
    private String spiceLevel;
    private String preferredCuisines;
    private String dietaryNotes;
    private String dislikedIngredients;
}