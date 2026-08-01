package com.example.dto;


import org.jspecify.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class LoginDTO {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

