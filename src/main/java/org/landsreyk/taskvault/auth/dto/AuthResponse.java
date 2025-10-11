package org.landsreyk.taskvault.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class AuthResponse {
    private final String accessToken;
    private final Instant expiresAt;
}
