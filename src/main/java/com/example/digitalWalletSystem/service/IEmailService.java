package com.example.digitalWalletSystem.service;

public interface IEmailService {
    void sendFraudAlert(String email, String message);
}
