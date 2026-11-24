package com.infy.scm360.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenValidator jwtTokenValidator;

    public JwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        ScmUserPrincipal principal = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            principal = jwtTokenValidator.validateAndExtract(token);
        }

        // Defensive check: if token was verified by Gateway and forwarded via convenience headers
        if (principal == null) {
            String userId = request.getHeader("X-User-Id");
            String rolesHeader = request.getHeader("X-User-Roles");
            String locationsHeader = request.getHeader("X-Location-Ids");
            String supplierId = request.getHeader("X-Supplier-Id");

            if (userId != null && !userId.isBlank()) {
                List<String> roles = rolesHeader != null ? Arrays.asList(rolesHeader.split(",")) : List.of();
                List<String> locations = locationsHeader != null && !locationsHeader.isBlank()
                        ? Arrays.asList(locationsHeader.split(",")) : List.of();
                principal = new ScmUserPrincipal(userId, userId, roles, locations, supplierId);
            }
        }

        if (principal != null) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
