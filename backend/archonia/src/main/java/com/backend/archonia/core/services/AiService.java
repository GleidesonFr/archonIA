package com.backend.archonia.core.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.backend.archonia.core.clients.AiClient;
import com.backend.archonia.core.dto.AiRequest;
import com.backend.archonia.core.dto.AiResponse;
import com.backend.archonia.models.ChatMessage;
import com.backend.archonia.models.UserSession;

@Service
public class AiService {
    
    private static final int MAX_HISTORY = 20;
    private final SessionService sessionService;
    private final AiClient aiClient;

    public AiService(SessionService sessionService, AiClient aiClient) {
        this.sessionService = sessionService;
        this.aiClient = aiClient;
    }

    public AiResponse handleUserMessage(String sessionId, String userMessage, String systemPrompt, String userId){
        if(sessionId == null || sessionId.isBlank()){
            sessionId = UUID.randomUUID().toString();
        }
        UserSession session = sessionService.getSession(sessionId, userId);
        
        ChatMessage userChatMessage = new ChatMessage("user", userMessage);
        session.addMessage(userChatMessage);

        List<ChatMessage> limitedHistory = applyHistoryLimit(session.getHistory());

        List<Map<String, String>> history = limitedHistory.stream()
            .map(msg -> Map.of("role", msg.getRole(), "content", msg.getContent()))
            .toList();

        AiRequest request = new AiRequest();
        request.setSessionId(session.getSessionId());
        request.setMessage(userMessage);
        request.setSystemPrompt(systemPrompt);
        request.setHistory(history);
        AiResponse response = aiClient.ask(request);

        ChatMessage aiChatMessage = new ChatMessage("assistant", response.getReply());
        session.addMessage(aiChatMessage);

        sessionService.updateSession(session);

        return response;
    }

    private List<ChatMessage> applyHistoryLimit(List<ChatMessage> history) {
        if (history.size() <= MAX_HISTORY) {
            return history;
        }

        return new ArrayList<>(
            history.subList(history.size() - MAX_HISTORY, history.size())
        );
    }
}
