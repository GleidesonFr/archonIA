package com.backend.archonia.core.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AiRequest {
    private String sessionId;
    private String message;
    private String systemPrompt;
    private List<Map<String, String>> history;
}
