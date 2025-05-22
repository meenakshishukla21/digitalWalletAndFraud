package com.example.digitalWalletSystem.controller;

import com.example.digitalWalletSystem.model.Transaction;
import com.example.digitalWalletSystem.model.Wallet;
import com.example.digitalWalletSystem.repository.UserRepository;
import com.example.digitalWalletSystem.repository.WalletRepository;
import com.example.digitalWalletSystem.service.IWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final IWalletService walletService;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletController(IWalletService walletService, WalletRepository walletRepository, UserRepository userRepository) {
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds to wallet", responses = {
            @ApiResponse(responseCode = "200", description = "Deposit successful"),
            @ApiResponse(responseCode = "400", description = "Invalid deposit amount"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<Transaction> deposit(@RequestParam BigDecimal amount) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Wallet wallet = walletRepository.findByUserId(getUserIdByUsername(username))
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        Transaction transaction = walletService.deposit(wallet.getId(), amount);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw funds from wallet", responses = {
            @ApiResponse(responseCode = "200", description = "Withdrawal successful"),
            @ApiResponse(responseCode = "400", description = "Invalid withdrawal amount or insufficient balance"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<Transaction> withdraw(@RequestParam BigDecimal amount) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Wallet wallet = walletRepository.findByUserId(getUserIdByUsername(username))
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        Transaction transaction = walletService.withdraw(wallet.getId(), amount);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds to another user", responses = {
            @ApiResponse(responseCode = "200", description = "Transfer successful"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer amount or insufficient balance"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<Transaction> transfer(@RequestParam Long toWalletId, @RequestParam BigDecimal amount) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Wallet fromWallet = walletRepository.findByUserId(getUserIdByUsername(username))
                .orElseThrow(() -> new IllegalArgumentException("Source wallet not found"));
        Transaction transaction = walletService.transfer(fromWallet.getId(), toWalletId, amount);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/history")
    @Operation(summary = "Get transaction history for user", responses = {
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<List<Transaction>> getTransactionHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Wallet wallet = walletRepository.findByUserId(getUserIdByUsername(username))
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        List<Transaction> transactions = walletService.getTransactionHistory(wallet.getId());
        return ResponseEntity.ok(transactions);
    }

    private Long getUserIdByUsername(String username) {
        // Implement logic to fetch user ID from username (e.g., via UserRepository)
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found")).getId();
    }
}