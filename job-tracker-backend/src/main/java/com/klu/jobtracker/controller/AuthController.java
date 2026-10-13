
package com.klu.jobtracker.controller;

import com.klu.jobtracker.dto.LoginRequest;
import com.klu.jobtracker.dto.LoginResponse;
import com.klu.jobtracker.dto.SignupRequest;
import com.klu.jobtracker.dto.UserResponse;
import com.klu.jobtracker.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(
            @Valid @RequestBody SignupRequest request) {

        UserResponse response = service.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signin(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = service.signin(request);
        return ResponseEntity.ok(response);
    }
}
