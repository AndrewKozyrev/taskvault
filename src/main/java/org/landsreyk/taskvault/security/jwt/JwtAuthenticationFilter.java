package org.landsreyk.taskvault.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.landsreyk.taskvault.security.TokenService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final BearerTokenResolver tokenResolver;
    private final AuthenticationEntryPoint entryPoint;
    private final JwtAuthoritiesConverter authoritiesConverter;

    private static final Set<String> PUBLIC_PREFIXES = Set.of("/api/health", "/api/auth");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        if (PUBLIC_PREFIXES.stream().anyMatch(request.getRequestURI()::startsWith)) {
            chain.doFilter(request, response);
            return;
        }
        Optional<String> tokenOpt = tokenResolver.resolve(request);
        if (tokenOpt.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }
        String token = tokenOpt.get();
        try {
            String username = tokenService.extractUsername(token);
            List<String> roles = tokenService.extractRoles(token);
            if (!roles.isEmpty()) {
                Collection<? extends GrantedAuthority> authorities = authoritiesConverter.fromRoles(roles);
                UserDetails tmpUser = org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password("N/A")
                        .authorities(authorities)
                        .build();
                if (!tokenService.isTokenValid(token, tmpUser)) {
                    entryPoint.commence(request, response, new BadCredentialsException("Invalid JWT"));
                    return;
                }
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext ctx = SecurityContextHolder.createEmptyContext();
                ctx.setAuthentication(auth);
                SecurityContextHolder.setContext(ctx);
                chain.doFilter(request, response);
                return;
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!tokenService.isTokenValid(token, userDetails)) {
                entryPoint.commence(request, response, new BadCredentialsException("Invalid JWT"));
                return;
            }
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(request);
            auth.setDetails(details);
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            chain.doFilter(request, response);
        } catch (Exception ex) {
            entryPoint.commence(request, response, new BadCredentialsException("Invalid JWT", ex));
        }
    }
}
