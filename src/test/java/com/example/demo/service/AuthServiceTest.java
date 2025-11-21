package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

@RestClientTest(AuthService.class)
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${supabase.url}")
    private String SUPABASE_URL;

    @Value("${supabase.anon-key}")
    private String SUPABASE_ANON_KEY;

    @Test
    @DisplayName("회원가입 서비스 테스트")
    void signUp() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .email("test@example.com")
                .password("password")
                .role("USER")
                .build();

        Map<String, Object> supabaseResponse = Map.of(
                "access_token", "access-token",
                "token_type", "bearer",
                "expires_in", 3600,
                "refresh_token", "refresh-token",
                "user", Map.of(
                        "id", "user-id",
                        "aud", "authenticated",
                        "email", "test@example.com",
                        "created_at", Instant.now().toString(),
                        "updated_at", Instant.now().toString(),
                        "user_metadata", Map.of("role", "USER")
                )
        );

        server.expect(requestTo(SUPABASE_URL + "/auth/v1/signup"))
                .andExpect(method(POST))
                .andExpect(header("apikey", SUPABASE_ANON_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(supabaseResponse), MediaType.APPLICATION_JSON));

        // when
        AuthResponse response = authService.signUp(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getUser().getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("로그인 서비스 테스트")
    void signIn() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("goodeden3@gmail.com")
                .password("123123")
                .build();

        Map<String, Object> supabaseResponse = Map.of(
                "access_token", "access-token",
                "token_type", "bearer",
                "expires_in", 3600,
                "refresh_token", "refresh-token",
                "user", Map.of(
                        "id", "user-id",
                        "aud", "authenticated",
                        "email", "goodeden3@gmail.com",
                        "created_at", Instant.now().toString(),
                        "updated_at", Instant.now().toString(),
                        "user_metadata", Map.of("role", "ADMIN")
                )
        );

        server.expect(requestTo(SUPABASE_URL + "/auth/v1/token?grant_type=password"))
                .andExpect(method(POST))
                .andExpect(header("apikey", SUPABASE_ANON_KEY))
                .andRespond(withSuccess(objectMapper.writeValueAsString(supabaseResponse), MediaType.APPLICATION_JSON));

        // when
        AuthResponse response = authService.signIn(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getUser().getEmail()).isEqualTo("goodeden3@gmail.com");
        assertThat(response.getUser().getRole()).isEqualTo("ADMIN");
    }
}
