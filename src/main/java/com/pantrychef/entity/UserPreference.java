package com.pantrychef.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spice_level", nullable = false, length = 20)
    private String spiceLevel = "medium";

    @Column(name = "preferred_cuisines", length = 500)
    private String preferredCuisines;

    @Column(name = "dietary_notes", length = 500)
    private String dietaryNotes;

    @Column(name = "disliked_ingredients", length = 500)
    private String dislikedIngredients;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}