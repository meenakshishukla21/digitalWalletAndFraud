package com.example.digitalWalletSystem.service.impl;

import com.example.digitalWalletSystem.model.Transaction;
import com.example.digitalWalletSystem.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FraudScheduler {

    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    public FraudScheduler(TransactionRepository transactionRepository, EmailService emailService) {
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Daily at midnight
    public void dailyFraudScan() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<Transaction> transactions = transactionRepository.findByCreatedAtAfter(oneDayAgo);
        for (Transaction tx : transactions) {
            if (tx.isFlagged()) {
                emailService.sendFraudAlert(tx.getFromWallet().getUser().getEmail(),
                        "Flagged transaction detected: " + tx.getId());
            }
        }
    }
}