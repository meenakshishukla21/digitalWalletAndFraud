package com.example.digitalWalletSystem.repository;

import com.example.digitalWalletSystem.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
    List<Wallet> findTop5ByOrderByBalanceDesc();
}