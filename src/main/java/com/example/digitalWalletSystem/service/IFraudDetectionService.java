package com.example.digitalWalletSystem.service;

import com.example.digitalWalletSystem.model.Transaction;
import com.example.digitalWalletSystem.model.Wallet;

public interface IFraudDetectionService {
    boolean checkForFraud(Wallet wallet, Transaction transaction);
}
