package com.example.digitalWalletSystem.controller;

import com.example.digitalWalletSystem.model.Transaction;
import com.example.digitalWalletSystem.model.Wallet;
import com.example.digitalWalletSystem.repository.TransactionRepository;
import com.example.digitalWalletSystem.repository.WalletRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public AdminController(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    @GetMapping("/flagged-transactions")
    @Operation(summary = "Get all flagged transactions")
    public List<Transaction> getFlaggedTransactions() {
        return transactionRepository.findByFlaggedTrue();
    }

    @GetMapping("/total-balances")
    @Operation(summary = "Get total balance of all wallets")
    public BigDecimal getTotalBalances() {
        return walletRepository.findAll().stream()
                .map(Wallet::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @GetMapping("/top-users")
    @Operation(summary = "Get top 5 users by balance")
    public List<Wallet> getTopUsers() {
        return walletRepository.findTop5ByOrderByBalanceDesc();
    }
}