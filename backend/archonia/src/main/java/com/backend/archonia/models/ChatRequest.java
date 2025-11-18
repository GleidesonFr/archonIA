package com.backend.archonia.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    
    @NotBlank(message = "sessionId is mandatory")
    private String sessionId;

    @NotBlank(message = "message is mandatory")
    private String message;

    @NotBlank(message = "message is mandatory")
    private String systemPrompt;
}
