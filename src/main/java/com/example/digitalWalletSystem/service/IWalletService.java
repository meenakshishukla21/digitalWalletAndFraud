package com.example.digitalWalletSystem.service;
import com.example.digitalWalletSystem.model.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface IWalletService {
    Transaction deposit(Long walletId, BigDecimal amount);
    Transaction withdraw(Long walletId, BigDecimal amount);
    Transaction transfer(Long fromWalletId, Long toWalletId, BigDecimal amount);
    List<Transaction> getTransactionHistory(Long walletId);
}
