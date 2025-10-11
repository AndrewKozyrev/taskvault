package org.landsreyk.taskvault.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class TokenService {

    private final JwtProperties props;
    private final SecretKey hmacKey;

    public TokenService(JwtProperties props) {
        this.props = props;
        String base64OrAsciiSecret = props.getSecret();
        byte[] keyBytes = Decoders.BASE64.decode(base64OrAsciiSecret);
        hmacKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserDetails principal, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(principal.getUsername())
                .claims(extraClaims)
                .issuer(props.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(props.getAccessTtl())))
                .signWith(hmacKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        Claims claims = extractAllClaims(token);
        Date expiration = claims.getExpiration();
        Instant now = Instant.now();
        return extractUsername(token).equals(user.getUsername()) && now.isBefore(expiration.toInstant());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(hmacKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
