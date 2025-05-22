package com.example.digitalWalletSystem.service.impl;

import com.example.digitalWalletSystem.model.Transaction;
import com.example.digitalWalletSystem.model.Wallet;
import com.example.digitalWalletSystem.repository.TransactionRepository;
import com.example.digitalWalletSystem.repository.WalletRepository;
import com.example.digitalWalletSystem.service.IWalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService  implements IWalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudDetectionService;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository,
                         FraudDetectionService fraudDetectionService) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.fraudDetectionService = fraudDetectionService;
    }

    @Transactional
    @Override
    public Transaction deposit(Long walletId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setToWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType("DEPOSIT");
        transaction.setFlagged(fraudDetectionService.checkForFraud(wallet, transaction));
        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public Transaction withdraw(Long walletId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setFromWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType("WITHDRAW");
        transaction.setFlagged(fraudDetectionService.checkForFraud(wallet, transaction));
        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public Transaction transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        Wallet fromWallet = walletRepository.findById(fromWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Source wallet not found"));
        Wallet toWallet = walletRepository.findById(toWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Target wallet not found"));
        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
        toWallet.setBalance(toWallet.getBalance().add(amount));
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        Transaction transaction = new Transaction();
        transaction.setFromWallet(fromWallet);
        transaction.setToWallet(toWallet);
        transaction.setAmount(amount);
        transaction.setType("TRANSFER");
        transaction.setFlagged(fraudDetectionService.checkForFraud(fromWallet, transaction));
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionHistory(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        return transactionRepository.findAll().stream()
                .filter(t -> !t.isDeleted() &&
                        ((t.getFromWallet() != null && t.getFromWallet().getId().equals(walletId)) ||
                                (t.getToWallet() != null && t.getToWallet().getId().equals(walletId))))
                .toList();
    }
}