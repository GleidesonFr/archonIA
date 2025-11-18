package com.gateway.archonia.models;

import lombok.Data;

@Data
public class GatewayRequest {
    private String message;
    private String sessionId;
}
