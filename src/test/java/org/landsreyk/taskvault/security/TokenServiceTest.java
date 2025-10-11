package org.landsreyk.taskvault.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenService tokenService;

    @Test
    void generateToken() {
        UserDetails user = userDetailsService.loadUserByUsername("user");
        String token = tokenService.generateAccessToken(user, null);
        System.out.println(token);
    }
}