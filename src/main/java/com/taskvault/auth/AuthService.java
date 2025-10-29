package com.taskvault.auth;

import com.taskvault.security.UserCredentials;
import com.taskvault.security.UserCredentialsRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserCredentialsRepository userCredentialsRepository,
                       PasswordEncoder passwordEncoder) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long register(String username, String rawPassword) {
        if (userCredentialsRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        UserCredentials credentials = new UserCredentials();
        credentials.setUsername(username);
        credentials.setPasswordHash(passwordEncoder.encode(rawPassword));
        credentials.setUserId(null);

        UserCredentials saved = userCredentialsRepository.save(credentials);
        return saved.getId();
    }
}