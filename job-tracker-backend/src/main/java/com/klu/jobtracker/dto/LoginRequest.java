package com.klu.jobtracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank @Email private String email;
    @NotBlank private String password;

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public void setEmail(String email) { this.email = email == null ? null : email.trim().toLowerCase(); }
    public void setPassword(String password) { this.password = password; }
}
