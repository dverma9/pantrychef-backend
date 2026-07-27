package com.pantrychef.controller;

import com.pantrychef.dto.IngredientDto;
import com.pantrychef.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pantry")
@RequiredArgsConstructor
public class PantryController {

    private final IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<List<IngredientDto>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    @PostMapping
    public ResponseEntity<IngredientDto> addIngredient(@Valid @RequestBody IngredientDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.addIngredient(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}