package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.dto.UserResponse;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${supabase.url}")
    private String SUPABASE_URL;

    @Value("${supabase.anon-key}")
    private String SUPABASE_ANON_KEY;

    private final RestClient.Builder restClientBuilder;

    public AuthResponse signUp(SignupRequest request) {
        RestClient restClient = restClientBuilder.build();
        
        Map<String, Object> requestBody = Map.of(
            "email", request.getEmail(),
            "password", request.getPassword(),
            "data", Map.of("role", request.getRole())
        );

        Map<String, Object> response = restClient.post()
            .uri(SUPABASE_URL + "/auth/v1/signup")
            .header("apikey", SUPABASE_ANON_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        return mapToAuthResponse(response);
    }

    public AuthResponse signIn(LoginRequest request) {
        RestClient restClient = restClientBuilder.build();

        Map<String, Object> requestBody = Map.of(
            "email", request.getEmail(),
            "password", request.getPassword()
        );

        Map<String, Object> response = restClient.post()
            .uri(SUPABASE_URL + "/auth/v1/token?grant_type=password")
            .header("apikey", SUPABASE_ANON_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        return mapToAuthResponse(response);
    }

    @SuppressWarnings("unchecked")
    private AuthResponse mapToAuthResponse(Map<String, Object> response) {
        if (response == null) return null;

        Map<String, Object> userMap = (Map<String, Object>) response.get("user");
        Map<String, Object> userMetadata = (Map<String, Object>) userMap.get("user_metadata");

        UserResponse userResponse = UserResponse.builder()
                .id((String) userMap.get("id"))
                .aud((String) userMap.get("aud"))
                .email((String) userMap.get("email"))
                .phone((String) userMap.get("phone"))
                .role(userMetadata != null ? (String) userMetadata.get("role") : null)
                .sub((String) userMap.get("sub"))
                .emailConfirmedAt(parseInstant(userMap.get("email_confirmed_at")))
                .confirmationSentAt(parseInstant(userMap.get("confirmation_sent_at")))
                .confirmedAt(parseInstant(userMap.get("confirmed_at")))
                .lastSignInAt(parseInstant(userMap.get("last_sign_in_at")))
                .phoneVerified(Boolean.TRUE.equals(userMap.get("phone_verified")))
                .createdAt(parseInstant(userMap.get("created_at")))
                .updatedAt(parseInstant(userMap.get("updated_at")))
                .isAnonymous(Boolean.TRUE.equals(userMap.get("is_anonymous")))
                .build();

        return AuthResponse.builder()
                .accessToken((String) response.get("access_token"))
                .tokenType((String) response.get("token_type"))
                .expiresIn((Integer) response.get("expires_in"))
                .refreshToken((String) response.get("refresh_token"))
                .user(userResponse)
                .build();
    }

    private Instant parseInstant(Object dateStr) {
        if (dateStr == null) return null;
        return Instant.parse((String) dateStr);
    }
}
