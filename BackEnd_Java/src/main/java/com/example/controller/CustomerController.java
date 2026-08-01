package com.example.controller;

import com.example.dto.ChangePasswordDTO;
import com.example.dto.CustomerDTO;
import com.example.dto.CustomerIdDTO;
import com.example.model.CustomerModel;
import com.example.services.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<CustomerModel> getProfile(Principal authentication) {

        String email = authentication.getName(); // comes from JWT subject
        CustomerModel customer = authService.getCustomerProfile(email);

        return ResponseEntity.ok(customer);
    }

    @PutMapping("/profile")
    public ResponseEntity<CustomerModel> updateProfile(
            Principal principal,
            @RequestBody CustomerDTO dto) {
        String email = principal.getName();
        CustomerModel updated = authService.updateCustomerProfile(email, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/id")
    public ResponseEntity<CustomerIdDTO> getProfileID(Principal authentication) {

        String email = authentication.getName(); // comes from JWT subject
        CustomerIdDTO customerId = authService.getCustomerIdByEmail(email);

        return ResponseEntity.ok(customerId);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Principal principal, @RequestBody ChangePasswordDTO dto) {
        String email = principal.getName();
        authService.changePassword(email, dto.getOldPassword(), dto.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }

}
