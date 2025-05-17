package org.example.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Allow public paths (e.g., login, register)
        if (path.startsWith("/api/security/auth")) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Missing or invalid Authorization header for path: " + path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Base64.getDecoder().decode(secretKey))
                    .parseClaimsJws(token)
                    .getBody();

            String userEmail = claims.getSubject();
            String permissions = claims.get("permissions", String.class);
            String userId = claims.get("userId", String.class);

            if (userEmail == null || permissions == null || userId == null) {
                System.out.println("❌ Missing required claims in JWT");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            System.out.println("✅ JWT valid for: " + userEmail);
            System.out.println("✅ JWT permissions: " + permissions);

            // Add user information to headers and preserve Authorization header
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-USER-ID", userId)
                    .header("X-USER-EMAIL", userEmail)
                    .header("X-PERMISSIONS", permissions)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)  // Preserve the original Authorization header
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            System.out.println("❌ JWT validation failed: " + e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        // Execute after LoggingFilter but before PermissionFilter
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}
