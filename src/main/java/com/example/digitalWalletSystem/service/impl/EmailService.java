package com.example.digitalWalletSystem.service.impl;

import com.example.digitalWalletSystem.service.IEmailService;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {

    @Override
    public void sendFraudAlert(String email, String message) {
        // Mock email sending
        System.out.println("Email sent to " + email + ": " + message);
    }
}