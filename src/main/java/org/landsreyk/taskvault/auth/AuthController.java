package org.landsreyk.taskvault.auth;

import lombok.RequiredArgsConstructor;
import org.landsreyk.taskvault.auth.dto.AuthRequest;
import org.landsreyk.taskvault.auth.dto.AuthResponse;
import org.landsreyk.taskvault.security.JwtProperties;
import org.landsreyk.taskvault.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final JwtProperties jwtProperties;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody AuthRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        UserDetails principal = (UserDetails) auth.getPrincipal();
        String token = tokenService.generateAccessToken(principal, Collections.emptyMap());
        Instant expiresAt = Instant.now().plus(jwtProperties.getAccessTtl());
        return ResponseEntity.ok(new AuthResponse(token, expiresAt));
    }
}
