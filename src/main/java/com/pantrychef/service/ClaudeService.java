package com.pantrychef.service;

import com.pantrychef.dto.ConversationMessage;
import com.pantrychef.entity.Ingredient;
import com.pantrychef.entity.UserPreference;
import com.pantrychef.repository.IngredientRepository;
import com.pantrychef.repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaudeService {

    private final RestTemplate restTemplate;
    private final IngredientRepository ingredientRepository;
    private final PreferenceRepository preferenceRepository;

    @Value("${claude.api.key}")
    private String claudeApiKey;

    @Value("${claude.api.url}")
    private String claudeApiUrl;

    @Value("${claude.model}")
    private String claudeModel;

    public String chat(String userMessage, List<ConversationMessage> history) {
        try {
            List<Ingredient> pantry = ingredientRepository.findAll();
            UserPreference prefs = preferenceRepository.findFirstBy().orElse(new UserPreference());

            String systemPrompt = buildSystemPrompt(pantry, prefs);
            List<Map<String, String>> messages = buildMessages(userMessage, history);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", claudeModel);
            requestBody.put("max_tokens", 1024);
            requestBody.put("system", systemPrompt);
            requestBody.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", claudeApiKey);
            headers.set("anthropic-version", "2023-06-01");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    claudeApiUrl, HttpMethod.POST, request, Map.class);

            Map responseBody = response.getBody();
            List<Map<String, Object>> content = (List<Map<String, Object>>) responseBody.get("content");
            return content.get(0).get("text").toString();

        } catch (Exception e) {
            log.error("Claude API error: {}", e.getMessage());
            throw new RuntimeException("AI service temporarily unavailable. Please try again.");
        }
    }

    private String buildSystemPrompt(List<Ingredient> pantry, UserPreference prefs) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are PantryChef AI, a warm, knowledgeable, and practical cooking companion.\n");
        sb.append("Your job is to help the user decide what to cook and how to cook it.\n\n");

        sb.append("=== USER'S PANTRY ===\n");
        if (pantry.isEmpty()) {
            sb.append("The pantry is currently empty. Ask the user to add some ingredients first.\n");
        } else {
            pantry.forEach(i -> sb.append("- ").append(i.getName())
                    .append(i.getQuantity() != null ? ": " + i.getQuantity() : "")
                    .append(i.getUnit() != null ? " " + i.getUnit() : "")
                    .append("\n"));
        }

        sb.append("\n=== USER'S PREFERENCES ===\n");
        sb.append("- Spice level: ").append(prefs.getSpiceLevel() != null ? prefs.getSpiceLevel() : "medium").append("\n");
        sb.append("- Preferred cuisines: ").append(prefs.getPreferredCuisines() != null ? prefs.getPreferredCuisines() : "any").append("\n");
        sb.append("- Dietary restrictions: ").append(prefs.getDietaryNotes() != null ? prefs.getDietaryNotes() : "none").append("\n");
        sb.append("- Disliked ingredients: ").append(prefs.getDislikedIngredients() != null ? prefs.getDislikedIngredients() : "none").append("\n");

        sb.append("\n=== YOUR RULES ===\n");
        sb.append("1. Always suggest dishes the user can make with their CURRENT pantry first.\n");
        sb.append("2. When asked for a recipe, provide: dish name, serving size, ingredients with quantities, numbered steps.\n");
        sb.append("3. When a user wants a dish they cannot make, list the MISSING ingredients clearly, one per line.\n");
        sb.append("4. Always respect spice level and dietary restrictions.\n");
        sb.append("5. Keep your tone warm, encouraging, and conversational.\n");
        sb.append("6. Keep responses concise unless a full recipe is requested.\n");

        return sb.toString();
    }

    private List<Map<String, String>> buildMessages(String userMessage, List<ConversationMessage> history) {
        List<Map<String, String>> messages = new ArrayList<>();

        if (history != null) {
            int start = Math.max(0, history.size() - 10);
            for (int i = start; i < history.size(); i++) {
                ConversationMessage msg = history.get(i);
                messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
            }
        }

        messages.add(Map.of("role", "user", "content", userMessage));
        return messages;
    }
}