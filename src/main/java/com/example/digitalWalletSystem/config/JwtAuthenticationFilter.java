package com.example.digitalWalletSystem.config;

import com.example.digitalWalletSystem.model.Token;
import com.example.digitalWalletSystem.repository.TokenRepository;
import com.example.digitalWalletSystem.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, TokenRepository tokenRepository, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            // Validate JWT token
            if (jwtUtil.validateJwtToken(token)) {
                String username = jwtUtil.getUsernameFromJwtToken(token);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // Optionally validate UUID token (if provided in a custom header)
            String sessionToken = request.getHeader("X-Session-Token");
            if (sessionToken != null && jwtUtil.validateUuidToken(sessionToken)) {
                Token storedToken = tokenRepository.findByTokenAndRevokedFalse(sessionToken)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid or revoked session token"));
                if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Session token expired");
                }
                // Additional validation if needed
            }
        }
        chain.doFilter(request, response);
    }
}