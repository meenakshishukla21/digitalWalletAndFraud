package com.example.digitalWalletSystem.repository;

import com.example.digitalWalletSystem.model.Transaction;
import com.example.digitalWalletSystem.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromWalletAndTypeAndCreatedAtAfter(Wallet wallet, String type, LocalDateTime createdAt);
    List<Transaction> findByFlaggedTrue();
    List<Transaction> findByCreatedAtAfter(LocalDateTime createdAt);
}