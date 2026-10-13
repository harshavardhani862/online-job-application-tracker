package com.klu.jobtracker.service;

import com.klu.jobtracker.dto.UserResponse;
import com.klu.jobtracker.entity.User;
import com.klu.jobtracker.exception.ResourceNotFoundException;
import com.klu.jobtracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponse profile(String email) {
        return new UserResponse(getByEmail(email));
    }

    public UserResponse updateProfile(String email, String name, String password, String phone) {
        User user = getByEmail(email);
        if (name != null && !name.isBlank()) user.setName(name.trim());
        if (password != null && !password.isBlank()) user.setPassword(passwordEncoder.encode(password));
        if (phone != null) user.setPhone(phone.trim());
        return new UserResponse(repository.save(user));
    }
}
