package org.example.securityservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        log.debug("🔍 Processing request to: {} {}", request.getMethod(), request.getRequestURI());
        log.debug("📨 Headers: {}", Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(h -> h, request::getHeader)));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("⚠️ No Bearer token found, proceeding with chain");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        log.debug("🔑 Extracted JWT token: {}", jwt);
        
        try {
            userEmail = jwtService.extractUsername(jwt);
            log.debug("👤 Extracted user email from token: {}", userEmail);
        } catch (Exception e) {
            log.error("❌ Failed to extract username from token", e);
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.debug("✅ Valid JWT token for user: {}", userEmail);
                // Extract permissions from JWT claims
                String permissions = jwtService.extractClaim(jwt, claims -> claims.get("permissions", String.class));
                log.debug("👮 User permissions: {}", permissions);

                List<SimpleGrantedAuthority> authorities = Arrays.stream(permissions.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

                log.debug("🔐 Granted authorities: {}", authorities);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities
                );
                
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("✅ Authentication set in SecurityContext");
            } else {
                log.warn("❌ Invalid JWT token");
            }
        }

        filterChain.doFilter(request, response);
    }
}
