package org.example.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class PermissionFilter implements GlobalFilter, Ordered {

    private final Map<Pattern, String> endpointPermissions;

    public PermissionFilter(Map<Pattern, String> endpointPermissions) {
        this.endpointPermissions = endpointPermissions;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Skip auth endpoints
        if (path.startsWith("/api/auth")) {
            return chain.filter(exchange);
        }

        // Get permissions from request header (set by JwtAuthFilter)
        String permissionsHeader = request.getHeaders().getFirst("X-PERMISSIONS");
        System.out.println("👮 Checking permissions for path: " + path);
        System.out.println("👮 User permissions: " + permissionsHeader);
        
        if (permissionsHeader == null) {
            System.out.println("❌ No permissions found in header");
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        List<String> userPermissions = Arrays.asList(permissionsHeader.split(","));

        // Check if any endpoint pattern matches and verify permissions
        boolean hasPermission = endpointPermissions.entrySet().stream()
                .filter(entry -> entry.getKey().matcher(path).matches())
                .peek(entry -> System.out.println("🎯 Matched pattern: " + entry.getKey() + ", Required permission: " + entry.getValue()))
                .findFirst()
                .map(entry -> userPermissions.contains(entry.getValue()))
                .orElse(true); // If no pattern matches, allow access

        if (!hasPermission) {
            System.out.println("❌ Permission denied");
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        System.out.println("✅ Permission granted");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Execute after JWT filter
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
} 