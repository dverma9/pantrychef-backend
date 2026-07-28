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

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public String chat(String userMessage, List<ConversationMessage> history) {
        try {
            List<Ingredient> pantry = ingredientRepository.findAll();
            UserPreference prefs = preferenceRepository.findFirstBy()
                    .orElse(new UserPreference());

            String systemPrompt = buildSystemPrompt(pantry, prefs);
            String fullPrompt = buildFullPrompt(systemPrompt, userMessage, history);

            Map<String, Object> requestBody = buildGeminiRequest(fullPrompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", geminiApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("Calling Gemini API at: {}", geminiApiUrl);
            ResponseEntity<Map> response = restTemplate.exchange(
                    geminiApiUrl, HttpMethod.POST, request, Map.class);

            return parseGeminiResponse(response.getBody());

        } catch (Exception e) {
            log.error("Gemini API error: {}", e.getMessage());
            throw new RuntimeException("AI service temporarily unavailable. Please try again.");
        }
    }

    private Map<String, Object> buildGeminiRequest(String prompt) {
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));
        content.put("role", "user");

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("maxOutputTokens", 1024);
        generationConfig.put("temperature", 0.7);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));
        requestBody.put("generationConfig", generationConfig);

        return requestBody;
    }

    @SuppressWarnings("unchecked")
    private String parseGeminiResponse(Map responseBody) {
        try {
            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) responseBody.get("candidates");
            Map<String, Object> content =
                    (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) content.get("parts");
            return parts.get(0).get("text").toString();
        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", responseBody);
            throw new RuntimeException("Failed to parse AI response.");
        }
    }

    private String buildFullPrompt(String systemPrompt, String userMessage,
                                    List<ConversationMessage> history) {
        StringBuilder sb = new StringBuilder();
        sb.append(systemPrompt).append("\n\n");

        if (history != null && !history.isEmpty()) {
            int start = Math.max(0, history.size() - 10);
            for (int i = start; i < history.size(); i++) {
                ConversationMessage msg = history.get(i);
                if ("user".equals(msg.getRole())) {
                    sb.append("User: ").append(msg.getContent()).append("\n");
                } else {
                    sb.append("PantryChef: ").append(msg.getContent()).append("\n");
                }
            }
            sb.append("\n");
        }

        sb.append("User: ").append(userMessage);
        return sb.toString();
    }

    private String buildSystemPrompt(List<Ingredient> pantry, UserPreference prefs) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are PantryChef AI, a warm, knowledgeable, and practical cooking companion. ");
        sb.append("Your job is to help the user decide what to cook and how to cook it. ");
        sb.append("Always respond directly without prefixing with 'PantryChef:'.\n\n");

        sb.append("=== USER'S PANTRY ===\n");
        if (pantry.isEmpty()) {
            sb.append("The pantry is currently empty. Ask the user to add ingredients first.\n");
        } else {
            pantry.forEach(i -> {
                sb.append("- ").append(i.getName());
                if (i.getQuantity() != null && !i.getQuantity().isBlank())
                    sb.append(": ").append(i.getQuantity());
                if (i.getUnit() != null && !i.getUnit().isBlank())
                    sb.append(" ").append(i.getUnit());
                sb.append("\n");
            });
        }

        sb.append("\n=== USER'S PREFERENCES ===\n");
        sb.append("- Spice level: ").append(
                prefs.getSpiceLevel() != null ? prefs.getSpiceLevel() : "medium").append("\n");
        sb.append("- Preferred cuisines: ").append(
                prefs.getPreferredCuisines() != null ? prefs.getPreferredCuisines() : "any").append("\n");
        sb.append("- Dietary restrictions: ").append(
                prefs.getDietaryNotes() != null ? prefs.getDietaryNotes() : "none").append("\n");
        sb.append("- Disliked ingredients: ").append(
                prefs.getDislikedIngredients() != null ? prefs.getDislikedIngredients() : "none").append("\n");

        sb.append("\n=== YOUR RULES ===\n");
        sb.append("1. Suggest dishes the user can make with their CURRENT pantry first.\n");
        sb.append("2. When asked for a recipe, provide: dish name, serving size, ");
        sb.append("ingredients with quantities, and numbered step-by-step instructions.\n");
        sb.append("3. When a user wants a dish they cannot make, list MISSING ingredients ");
        sb.append("clearly, one per line. Mark the most important one to order first.\n");
        sb.append("4. Always respect spice level and dietary restrictions.\n");
        sb.append("5. Keep your tone warm, encouraging, and conversational.\n");
        sb.append("6. Keep responses concise unless a full recipe is requested.\n");
        sb.append("7. Format recipes with clear numbered steps and ingredient lists.\n");

        return sb.toString();
    }
}