package com.gateway.archonia.services;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GuardrailsService {
    
    public static final List<String> bannedWords = List.of(
        "illegal",
        "explicit",
        "offensive",
        "harmful",
        "self-harm",
        "violence",
        "private information",
        "biased",
        "stereotypes"
    );

    public boolean isBlocked(String content){
        String lower = content.toLowerCase();
        return bannedWords.stream().anyMatch(lower::contains);
    }
}
