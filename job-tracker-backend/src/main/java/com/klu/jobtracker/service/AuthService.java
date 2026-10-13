package com.klu.jobtracker.service;

import com.klu.jobtracker.dto.*;
import com.klu.jobtracker.entity.User;
import com.klu.jobtracker.exception.EmailAlreadyRegisteredException;
import com.klu.jobtracker.repository.UserRepository;
import com.klu.jobtracker.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponse signup(SignupRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        String phone = request.getPhone() == null ? "" : request.getPhone().trim();

        User user = new User(request.getName().trim(), email,
                passwordEncoder.encode(request.getPassword()), phone);
        return new UserResponse(userRepository.save(user));
    }

    public LoginResponse signin(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtService.generateToken(email);
        return new LoginResponse(token, new UserResponse(user));
    }
}
