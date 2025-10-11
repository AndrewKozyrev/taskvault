package org.landsreyk.taskvault.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;

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
}