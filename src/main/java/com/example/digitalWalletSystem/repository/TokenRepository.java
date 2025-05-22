package com.example.digitalWalletSystem.repository;

import com.example.digitalWalletSystem.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    void deleteByUserId(Long userId);
}