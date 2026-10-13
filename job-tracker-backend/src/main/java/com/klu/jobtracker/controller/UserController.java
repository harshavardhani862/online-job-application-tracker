package com.klu.jobtracker.controller;

import com.klu.jobtracker.dto.UserResponse;
import com.klu.jobtracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users/profile")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<UserResponse> profile(Principal principal) {
        return ResponseEntity.ok(
                service.profile(principal.getName())
        );
    }

    @PutMapping
    public ResponseEntity<UserResponse> update(
            Principal principal,
            @RequestBody Map<String, String> body) {

        return ResponseEntity.ok(
                service.updateProfile(
                        principal.getName(),
                        body.get("name"),
                        body.get("password"),
                        body.get("phone")
                )
        );
    }
}