package com.backend.archonia.core.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.backend.archonia.core.dto.AiRequest;
import com.backend.archonia.core.dto.AiResponse;

@FeignClient(
    name = "ai-service",
    url = "${ai.service.url}" // URL configurável via application.properties
)
public interface AiClient {
    
    @PostMapping
    AiResponse ask(@RequestBody AiRequest request);
}
