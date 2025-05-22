package com.example.digitalWalletSystem.controller;

import com.example.digitalWalletSystem.config.JwtUtil;
import com.example.digitalWalletSystem.model.Token;
import com.example.digitalWalletSystem.model.User;
import com.example.digitalWalletSystem.model.Wallet;
import com.example.digitalWalletSystem.repository.TokenRepository;
import com.example.digitalWalletSystem.repository.UserRepository;
import com.example.digitalWalletSystem.repository.WalletRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public AuthController(UserRepository userRepository, WalletRepository walletRepository,
                          PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                          TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", responses = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Username or email already exists")
    })
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        if (userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
            return ResponseEntity.badRequest().body("Username or email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        walletRepository.save(wallet);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get tokens", responses = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // Generate JWT token
        String jwtToken = jwtUtil.generateJwtToken(username);

        // Generate and store UUID token
        String uuidToken = jwtUtil.generateUuidToken();
        Token token = new Token();
        token.setToken(uuidToken);
        token.setUser(user);
        token.setIssuedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusSeconds(jwtUtil.getExpiration() / 1000));
        tokenRepository.save(token);

        Map<String, String> response = new HashMap<>();
        response.put("jwtToken", jwtToken);
        response.put("sessionToken", uuidToken);
        return ResponseEntity.ok(response);
    }
}