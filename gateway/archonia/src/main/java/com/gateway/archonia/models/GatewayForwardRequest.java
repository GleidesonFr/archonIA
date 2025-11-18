package com.gateway.archonia.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GatewayForwardRequest {
    private String message;
    private String systemPrompt;
    private String sessionId;
}
