package com.gateway.archonia.filters;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.gateway.archonia.services.GuardrailsService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class GuardrailsFilter implements GlobalFilter, Ordered{

    @Autowired
    private GuardrailsService guardrailsService;

    @Override
    public int getOrder() {
        return -2;
    }

    @SuppressWarnings("null")
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(!exchange.getRequest().getPath().toString().contains("/api/v1/chat/")){
            return chain.filter(exchange);
        }

        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();
        return DataBufferUtils.join(requestBody)
        .flatMap(body -> {
            byte[] bytes;
            try(InputStream is = body.asInputStream()){
                bytes = is.readAllBytes();
            } catch (Exception e) {
                return Mono.error(e);
            }

            DataBufferUtils.release(body);
            String requestBodyString = new String(bytes, StandardCharsets.UTF_8);
            
            if(guardrailsService.isBlocked(requestBodyString)){
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                byte[] msgBytes = "Mensagem bloqueada pelo guardrail".getBytes();
                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory()
                    .wrap(msgBytes)));
            }

            //Recriar o corpo da requisição para seguir o fluxo
            Flux<DataBuffer> cachedBody = Flux.defer(() -> {
                DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(bytes);
                return Mono.just(buffer);
            });

            ServerHttpRequestDecorator mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return cachedBody;
                }
            };

            return chain.filter(
                exchange.mutate().request(mutatedRequest).build()
            );
        });
    }

    
    
}
