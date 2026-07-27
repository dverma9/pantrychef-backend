package com.pantrychef.controller;

import com.pantrychef.dto.ChatRequest;
import com.pantrychef.dto.ChatResponse;
import com.pantrychef.service.ClaudeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ClaudeService claudeService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String reply = claudeService.chat(request.getMessage(), request.getConversationHistory());
        return ResponseEntity.ok(ChatResponse.builder()
                .reply(reply)
                .timestamp(LocalDateTime.now())
                .build());
    }
}