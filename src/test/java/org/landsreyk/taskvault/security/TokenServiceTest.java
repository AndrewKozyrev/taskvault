package org.landsreyk.taskvault.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenService tokenService;

    @Test
    void generateToken() {
        var user = userDetailsService.loadUserByUsername("user");
        var token = tokenService.generateAccessToken(user, null);
        assertNotNull(token);
    }

    @Test
    void extractUsernameFromToken() {
        var user = userDetailsService.loadUserByUsername("user");
        var token = tokenService.generateAccessToken(user, null);
        var username = tokenService.extractUsername(token);
        assertEquals("user", username);
    }

    @Test
    void checkIfTokenIsValid() {
        var user = userDetailsService.loadUserByUsername("user");
        var token = tokenService.generateAccessToken(user, null);
        assertTrue(tokenService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_returnsFalse_forTamperedToken() {
        var userDetails = userDetailsService.loadUserByUsername("user");
        var token = tokenService.generateAccessToken(userDetails, Map.of());
        assertTrue(tokenService.isTokenValid(token, userDetails)); // control

        var tampered = token + "1";
        assertFalse(tokenService.isTokenValid(tampered, userDetails));
        assertThrows(JwtException.class, () -> tokenService.extractUsername(tampered));
    }
}