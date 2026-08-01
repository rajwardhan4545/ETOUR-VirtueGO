package com.example.controller;

import com.example.dto.CustomerDTO;
import com.example.dto.ForgotPasswordDTO;
import com.example.dto.GoogleLoginRequestDTO;
import com.example.dto.LoginDTO;
import com.example.dto.ResetPasswordDTO;
import com.example.model.CustomerModel;
import com.example.services.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    // PUBLIC REGISTER
    @PostMapping("/register")
    public ResponseEntity<CustomerModel> register(@Valid @RequestBody CustomerDTO dto) {
        logger.info("Attempting to register user");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(dto));
    }

    // PUBLIC LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO dto) {
        logger.info("Attempting to logging user");
        String token = authService.login(dto);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> sendResetToken(
            @RequestBody ForgotPasswordDTO dto) {

        logger.info("Password reset requested for email: {}", dto.getEmail());

        authService.sendResetToken(dto.getEmail());

        return ResponseEntity.ok(
                "Password reset link has been sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordDTO dto) {

        logger.info("Attempting to reset password");

        authService.resetPassword(dto);

        return ResponseEntity.ok(
                "Password has been reset successfully");
    }

    @PostMapping("/google-login")
    public ResponseEntity<Map<String, String>> googleLogin(@RequestBody GoogleLoginRequestDTO dto) {
        logger.info("Attempting google login (ID Token verification)");
        String token = authService.googleLogin(dto.getIdToken());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

}
