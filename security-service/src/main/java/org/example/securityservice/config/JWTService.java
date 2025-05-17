package org.example.securityservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Base64;

@Service
@Slf4j
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        log.debug("🔑 Extracting username from token");
        String username = extractClaim(token, Claims::getSubject);
        log.debug("👤 Extracted username: {}", username);
        return username;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        log.debug("📄 Extracted claims: {}", claims);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // ✅ Extract roles from the user
        String roles = userDetails.getAuthorities().stream()
                .map(role -> role.getAuthority())
                .collect(Collectors.joining(","));
        extraClaims.put("roles", roles);

        return generateToken(extraClaims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        log.debug("🎯 Generating token for user: {}", userDetails.getUsername());
        log.debug("➕ Extra claims: {}", extraClaims);
        
        String token = Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
        
        log.debug("✨ Generated token: {}", token);
        return token;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            log.debug("🔍 Validating token for user: {}", username);
            log.debug("🔄 Comparing with UserDetails username: {}", userDetails.getUsername());
            log.debug("⏰ Token expired? {}", isTokenExpired(token));
            boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
            log.debug("✅ Token valid? {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.error("❌ Token validation failed", e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            log.error("❌ Failed to extract claims from token", e);
            throw e;
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
} 