package com.example.controller;

import com.example.dto.CustomerDTO;
import com.example.model.CustomerModel;
import com.example.services.RegistrationClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class RegistrationClientController {

    @Autowired
    private RegistrationClientService registrationClientService;

    @PostMapping("/register")
    public ResponseEntity<CustomerModel> registerViaClient(
            @RequestBody CustomerDTO dto) {

        CustomerModel response = registrationClientService.registerCustomer(dto);

        return ResponseEntity.ok(response);
    }
}