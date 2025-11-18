package com.backend.archonia.core.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.backend.archonia.core.clients.AiClient;
import com.backend.archonia.core.dto.AiRequest;
import com.backend.archonia.core.dto.AiResponse;
import com.backend.archonia.models.ChatMessage;
import com.backend.archonia.models.UserSession;

@Service
public class AiService {
    
    private final SessionService sessionService;
    private final AiClient aiClient;

    public AiService(SessionService sessionService, AiClient aiClient) {
        this.sessionService = sessionService;
        this.aiClient = aiClient;
    }

    public AiResponse handleUserMessage(String sessionId, String userMessage, String systemPrompt){
        UserSession session = sessionService.getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        
        ChatMessage userChatMessage = new ChatMessage("user", userMessage);
        session.addMessage(userChatMessage);

        List<Map<String, String>> history = session.getHistory().stream()
            .map(msg -> Map.of("role", msg.getRole(), "content", msg.getContent()))
            .toList();

        AiRequest request = new AiRequest();
        request.setSessionId(sessionId);
        request.setMessage(userMessage);
        request.setSystemPrompt(systemPrompt);
        request.setHistory(history);
        AiResponse response = aiClient.ask(request);

        ChatMessage aiChatMessage = new ChatMessage("assistant", response.getReply());
        session.addMessage(aiChatMessage);

        sessionService.updateSession(session);

        return response;
    }
}
