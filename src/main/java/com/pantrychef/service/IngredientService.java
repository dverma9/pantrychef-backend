package com.pantrychef.service;

import com.pantrychef.dto.IngredientDto;
import com.pantrychef.entity.Ingredient;
import com.pantrychef.exception.ResourceNotFoundException;
import com.pantrychef.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public List<IngredientDto> getAllIngredients() {
        return ingredientRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public IngredientDto addIngredient(IngredientDto dto) {
        Ingredient ingredient = Ingredient.builder()
                .name(dto.getName())
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .build();
        return toDto(ingredientRepository.save(ingredient));
    }

    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingredient with id " + id + " not found");
        }
        ingredientRepository.deleteById(id);
    }

    private IngredientDto toDto(Ingredient ingredient) {
        return IngredientDto.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .quantity(ingredient.getQuantity())
                .unit(ingredient.getUnit())
                .createdAt(ingredient.getCreatedAt())
                .build();
    }
}