package org.landsreyk.taskvault.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TokenService {

    private final JwtProperties props;
    private final SecretKey signingKey;

    public TokenService(JwtProperties props) {
        this.props = props;
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.getSecret()));
    }

    public String generateAccessToken(UserDetails principal, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(principal.getUsername())
                .claims(extraClaims)
                .issuer(props.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(props.getAccessTtl())))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseSigned(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = parseSigned(token);
        Object raw = claims.get("roles");

        if (raw == null) {
            return Collections.emptyList();
        }

        if (raw instanceof Collection<?> c) {
            return c.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(String::trim)
                    .filter(x -> !x.isEmpty())
                    .distinct()
                    .toList();
        }
        return List.of(raw.toString());
    }

    public boolean isTokenValid(String token, UserDetails user) {
        try {
            Claims claims = parseSigned(token);
            if (!user.getUsername().equals(claims.getSubject())) {
                return false;
            }
            Instant exp = claims.getExpiration().toInstant();
            return exp.isAfter(Instant.now());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseSigned(String token) {
        return Jwts
                .parser()
                .verifyWith(signingKey)
                .requireIssuer(props.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
