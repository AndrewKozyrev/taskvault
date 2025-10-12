package org.landsreyk.taskvault.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.landsreyk.taskvault.security.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final BearerTokenResolver tokenResolver;

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
        String username = tokenService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!tokenService.isTokenValid(token, userDetails)) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(request);
        auth.setDetails(details);
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }
}
