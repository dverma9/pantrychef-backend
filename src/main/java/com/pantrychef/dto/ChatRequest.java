package com.pantrychef.dto;

import com.pantrychef.dto.ConversationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ChatRequest {

    @NotBlank(message = "Message cannot be blank.")
    @Size(max = 2000, message = "Message is too long.")
    private String message;

    private List<ConversationMessage> conversationHistory;
}