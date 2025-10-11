package org.landsreyk.taskvault.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String issuer;
    private Duration accessTtl;
    private String secret;
}
