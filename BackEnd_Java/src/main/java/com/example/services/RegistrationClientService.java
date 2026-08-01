package com.example.services;

import com.example.dto.CustomerDTO;
import com.example.model.CustomerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RegistrationClientService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String REGISTER_URL =
            "http://localhost:8082/auth/register";

    public CustomerModel registerCustomer(CustomerDTO dto) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CustomerDTO> request =
                new HttpEntity<>(dto, headers);

        ResponseEntity<CustomerModel> response =
                restTemplate.exchange(
                        REGISTER_URL,
                        HttpMethod.POST,
                        request,
                        CustomerModel.class
                );

        return response.getBody();
    }
}
