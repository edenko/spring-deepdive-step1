package com.example.demo.security;

import com.example.demo.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class JwtTokenValidator {

    @Value("${supabase.jwt-secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmail(Claims claims) {
        return claims.get("email", String.class);
    }

    public UserRole getRole(Claims claims) {
        // Supabase stores custom user metadata in "user_metadata" or "app_metadata"
        // Adjust this based on where you store the role in Supabase
        Map<String, Object> userMetadata = claims.get("user_metadata", Map.class);
        if (userMetadata != null && userMetadata.containsKey("role")) {
            String roleStr = (String) userMetadata.get("role");
            try {
                return UserRole.valueOf(roleStr);
            } catch (IllegalArgumentException e) {
                return UserRole.USER; // Default to USER if role is invalid
            }
        }
        
        // Fallback: check app_metadata if not in user_metadata
        Map<String, Object> appMetadata = claims.get("app_metadata", Map.class);
        if (appMetadata != null && appMetadata.containsKey("role")) {
            String roleStr = (String) appMetadata.get("role");
            try {
                return UserRole.valueOf(roleStr);
            } catch (IllegalArgumentException e) {
                return UserRole.USER;
            }
        }

        return UserRole.USER; // Default role
    }
}
