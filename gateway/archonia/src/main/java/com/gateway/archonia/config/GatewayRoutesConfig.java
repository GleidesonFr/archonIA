package com.gateway.archonia.config;

import org.springframework.cloud.gateway.filter.factory.CacheRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    private final CacheRequestBodyGatewayFilterFactory cacheRequestBodyFilter;

    public GatewayRoutesConfig(CacheRequestBodyGatewayFilterFactory cacheRequestBodyFilter) {
        this.cacheRequestBodyFilter = cacheRequestBodyFilter;
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        CacheRequestBodyGatewayFilterFactory.Config cacheConfig = new CacheRequestBodyGatewayFilterFactory.Config();

        cacheConfig.setBodyClass(byte[].class);

        return builder.routes()
        .route("session-backend", r -> r.path("/api/v1/chat/**")
        .filters(f -> f.stripPrefix(2).filter(cacheRequestBodyFilter.apply(cacheConfig)))
        .uri("http://localhost:8081"))
        .route("swagger-backend", r -> r.path("/swagger-ui.html","/swagger-ui/**","/v3/api-docs","/v3/api-docs/**").uri("http://localhost:8081"))
        .route("archon-health", r -> r.path("/health").uri("http://localhost:8081"))
        .route("sessions-chat", r -> r.path("/session/**").uri("http://localhost:8081"))
        .build();
    }
}
