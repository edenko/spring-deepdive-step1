package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String aud;
    private String role; // Extracted from user_metadata
    private String email;
    private String phone;
    private String sub; // Extracted from user_metadata

    @JsonProperty("email_confirmed_at")
    private Instant emailConfirmedAt;

    @JsonProperty("confirmation_sent_at")
    private Instant confirmationSentAt;

    @JsonProperty("confirmed_at")
    private Instant confirmedAt;

    @JsonProperty("last_sign_in_at")
    private Instant lastSignInAt;

    @JsonProperty("phone_verified")
    private boolean phoneVerified;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    @JsonProperty("is_anonymous")
    private boolean isAnonymous;
}
