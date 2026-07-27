package com.pantrychef.controller;

import com.pantrychef.dto.PreferenceDto;
import com.pantrychef.service.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @GetMapping
    public ResponseEntity<PreferenceDto> getPreferences() {
        return ResponseEntity.ok(preferenceService.getPreferences());
    }

    @PutMapping
    public ResponseEntity<PreferenceDto> updatePreferences(@RequestBody PreferenceDto dto) {
        return ResponseEntity.ok(preferenceService.upsertPreferences(dto));
    }
}