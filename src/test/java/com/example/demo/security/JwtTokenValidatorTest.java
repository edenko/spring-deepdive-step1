package com.example.demo.security;

import com.example.demo.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtTokenValidatorTest {

    private JwtTokenValidator jwtTokenValidator;
    private String secret = "huGoHDhrPeNww1NjCVZv4mMePdscpRfq67ZYUfBJb9kEjJuyRh4uWc5m6VJGYdYpYN1zCP7GR31vopBl4FLAgw==";

    @BeforeEach
    void setUp() {
        jwtTokenValidator = new JwtTokenValidator();
        ReflectionTestUtils.setField(jwtTokenValidator, "jwtSecret", secret);
    }

    @Test
    void validateToken_ShouldReturnClaims_WhenTokenIsValid() {
        // Arrange
        String email = "test@example.com";
        String token = Jwts.builder()
                .claim("email", email)
                .claim("user_metadata", Map.of("role", "ADMIN"))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        // Act
        Claims claims = jwtTokenValidator.validateToken(token);

        // Assert
        assertNotNull(claims);
        assertEquals(email, jwtTokenValidator.getEmail(claims));
        assertEquals(UserRole.ADMIN, jwtTokenValidator.getRole(claims));
    }

    @Test
    void getRole_ShouldReturnUser_WhenRoleIsMissing() {
        // Arrange
        String token = Jwts.builder()
                .claim("email", "user@example.com")
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
        Claims claims = jwtTokenValidator.validateToken(token);

        // Act
        UserRole role = jwtTokenValidator.getRole(claims);

        // Assert
        assertEquals(UserRole.USER, role);
    }
}
