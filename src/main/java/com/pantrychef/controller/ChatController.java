package com.pantrychef.controller;

import com.pantrychef.dto.ChatRequest;
import com.pantrychef.dto.ChatResponse;
import com.pantrychef.service.ClaudeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ClaudeService claudeService;

    @PostMapping
    public ResponseEntity<?> chat(@Valid @RequestBody ChatRequest request) {
        try {
            String reply = claudeService.chat(
                    request.getMessage(),
                    request.getConversationHistory()
            );
            return ResponseEntity.ok(ChatResponse.builder()
                    .reply(reply)
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "BAD_REQUEST", "message", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Chat error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "AI_UNAVAILABLE",
                            "message", "AI service temporarily unavailable. Please try again."));
        }
    }
}