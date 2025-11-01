package org.landsreyk.taskvault.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class JwtAuthoritiesConverter {

    public Collection<? extends GrantedAuthority> fromRoles(List<String> roles) {
        return roles.stream()
                .filter(Objects::nonNull)
                .filter(r -> !r.isBlank())
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
