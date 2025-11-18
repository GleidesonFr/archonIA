package com.backend.archonia.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.archonia.core.dto.AiResponse;
import com.backend.archonia.core.services.AiService;
import com.backend.archonia.models.ChatRequest;
import com.backend.archonia.models.ChatResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    private final AiService aiService;

    public ChatController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/send")
    public ChatResponse handleMessage(@Valid @RequestBody ChatRequest request) {
        
        AiResponse aiResponse = aiService.handleUserMessage(request.getSessionId(), request.getMessage(), request.getSystemPrompt());
        
        return new ChatResponse(request.getSessionId(), aiResponse.getReply());
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "Ok - ArchonIA backend operational";
    }
    
}
