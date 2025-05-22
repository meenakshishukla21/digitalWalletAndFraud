package com.example.digitalWalletSystem.service.impl;

import com.example.digitalWalletSystem.model.Transaction;
import com.example.digitalWalletSystem.model.Wallet;
import com.example.digitalWalletSystem.repository.TransactionRepository;
import com.example.digitalWalletSystem.service.IFraudDetectionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FraudDetectionService implements IFraudDetectionService {

    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    public FraudDetectionService(TransactionRepository transactionRepository, EmailService emailService) {
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
    }

    @Override
    public boolean checkForFraud(Wallet wallet, Transaction transaction) {
        boolean isFlagged = false;

        // Rule 1: More than 5 transfers in 1 hour
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Transaction> recentTransfers = transactionRepository.findByFromWalletAndTypeAndCreatedAtAfter(
                wallet, "TRANSFER", oneHourAgo);
        if (recentTransfers.size() >= 5) {
            isFlagged = true;
            emailService.sendFraudAlert(wallet.getUser().getEmail(), "Multiple transfers detected");
        }

        // Rule 2: Withdrawal > 50% of balance
        if (transaction.getType().equals("WITHDRAW") &&
                transaction.getAmount().compareTo(wallet.getBalance().multiply(new BigDecimal("0.5"))) > 0) {
            isFlagged = true;
            emailService.sendFraudAlert(wallet.getUser().getEmail(), "Large withdrawal detected");
        }

        return isFlagged;
    }
}