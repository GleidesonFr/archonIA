package com.backend.archonia.controllers;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.archonia.core.services.SessionService;
import com.backend.archonia.models.UserSession;

@RestController
@RequestMapping("/session")
public class SessionController {
    
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<UserSession> createSession(@RequestParam String userId){
        UserSession session = sessionService.createSession(userId);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<UserSession> getSession(@PathVariable String sessionId, @RequestParam String userId){
        UserSession session = sessionService.getSession(sessionId, userId);
        if (session != null) {
            return ResponseEntity.ok(session);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId, @RequestParam String userId){
        UserSession session = sessionService.getSession(sessionId, userId);

        if(session != null && session.getUserId().equals(userId)){
            sessionService.clearSession(sessionId);
            return ResponseEntity.noContent().build();            
        }

        return ResponseEntity.notFound().build();

    }

    @GetMapping
    public ResponseEntity<Set<Object>> getUserSessions(@RequestParam String userId) {
        Set<Object> sessions = sessionService.getSessionsbyUser(userId);
        return ResponseEntity.ok(sessions);
    }
    
}
