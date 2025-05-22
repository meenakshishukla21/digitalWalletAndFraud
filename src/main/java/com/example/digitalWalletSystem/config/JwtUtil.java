package com.example.digitalWalletSystem.config;

import com.example.digitalWalletSystem.model.Token;
import com.example.digitalWalletSystem.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtUtil {

    private final SecretKey secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
    private final long expiration = 3600000; // 1 hour in milliseconds
    private final TokenRepository tokenRepository;

    public JwtUtil(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        Date issuedAt = new Date();
        Date expiresAt = new Date(System.currentTimeMillis() + expiration);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            // Check JWT signature
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            // Check database for token validity
            Optional<Token> tokenOpt = tokenRepository.findByToken(token);
            if (tokenOpt.isEmpty()) {
                return false; // Token not found in database
            }
            Token dbToken = tokenOpt.get();
            return !dbToken.isRevoked() && dbToken.getExpiresAt().isAfter(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }
}