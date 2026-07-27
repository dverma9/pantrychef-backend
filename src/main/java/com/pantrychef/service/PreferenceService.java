package com.pantrychef.service;

import com.pantrychef.dto.PreferenceDto;
import com.pantrychef.entity.UserPreference;
import com.pantrychef.repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;

    public PreferenceDto getPreferences() {
        return preferenceRepository.findFirstBy()
                .map(this::toDto)
                .orElse(new PreferenceDto(null, "medium", null, null, null));
    }

    public PreferenceDto upsertPreferences(PreferenceDto dto) {
        UserPreference preference = preferenceRepository.findFirstBy()
                .orElse(new UserPreference());
        preference.setSpiceLevel(dto.getSpiceLevel() != null ? dto.getSpiceLevel() : "medium");
        preference.setPreferredCuisines(dto.getPreferredCuisines());
        preference.setDietaryNotes(dto.getDietaryNotes());
        preference.setDislikedIngredients(dto.getDislikedIngredients());
        return toDto(preferenceRepository.save(preference));
    }

    private PreferenceDto toDto(UserPreference pref) {
        return PreferenceDto.builder()
                .id(pref.getId())
                .spiceLevel(pref.getSpiceLevel())
                .preferredCuisines(pref.getPreferredCuisines())
                .dietaryNotes(pref.getDietaryNotes())
                .dislikedIngredients(pref.getDislikedIngredients())
                .build();
    }
}