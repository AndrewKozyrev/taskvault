package org.landsreyk.taskvault.security.jwt;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class BearerTokenResolver {

    /**
     * Extracts the token from the Authorization header if it is in
     * the form: "Bearer <token>" (case-insensitive for the word Bearer).
     */
    public Optional<String> resolve(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank()) {
            return Optional.empty();
        }
        if (!header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return Optional.empty();
        }
        return Optional.of(header.substring(7).trim());
    }
}
