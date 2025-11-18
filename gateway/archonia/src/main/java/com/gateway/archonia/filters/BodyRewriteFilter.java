package com.gateway.archonia.filters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.archonia.models.GatewayForwardRequest;
import com.gateway.archonia.models.GatewayRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BodyRewriteFilter implements GlobalFilter, Ordered{

    @Autowired
    @Qualifier("systemPrompt")
    private String systemPrompt;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    if (!exchange.getRequest().getPath().toString().contains("/api/v1/chat/")) {
        return chain.filter(exchange);
    }

    return DataBufferUtils.join(exchange.getRequest().getBody())
        .flatMap(dataBuffer -> {

            byte[] bytes;
            try (InputStream inputStream = dataBuffer.asInputStream()) {
                bytes = inputStream.readAllBytes();
            } catch (IOException e) {
                return Mono.error(e);
            }

            DataBufferUtils.release(dataBuffer);

            String bodyString = new String(bytes, StandardCharsets.UTF_8);

            GatewayRequest original;
            try {
                original = objectMapper.readValue(bodyString, GatewayRequest.class);
            } catch (Exception e) {
                return Mono.error(e);
            }

            GatewayForwardRequest transformed = new GatewayForwardRequest(
                original.getMessage(),
                systemPrompt,
                original.getSessionId()
            );

            byte[] newBodyBytes;
            try {
                newBodyBytes = objectMapper.writeValueAsBytes(transformed);
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }

            // Flux que devolve o novo body ao controller
            byte[] finalBytes = newBodyBytes;
            Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                @SuppressWarnings("null")
                DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(finalBytes);
                return Mono.just(buffer);
            });

            ServerHttpRequestDecorator decoratedRequest =
            new ServerHttpRequestDecorator(exchange.getRequest().mutate()
                .headers(h -> {
                    h.setContentType(MediaType.APPLICATION_JSON);
                    h.setContentLength(finalBytes.length);
                })
                .build()) {
                    @SuppressWarnings("null")
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return cachedFlux;
                    }
            };

            return chain.filter(
                exchange.mutate()
                    .request(decoratedRequest)
                    .build()
            );
        });
}
    
}