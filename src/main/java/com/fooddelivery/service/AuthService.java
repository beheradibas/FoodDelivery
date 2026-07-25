package com.fooddelivery.service;

import com.fooddelivery.dto.auth.AuthResponse;
import com.fooddelivery.dto.auth.LoginRequest;
import com.fooddelivery.dto.auth.RegisterRequest;
import com.fooddelivery.dto.user.CreateUserRequest;
import com.fooddelivery.dto.user.UserResponse;
import com.fooddelivery.entity.Role;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        return userService.createUser(new CreateUserRequest(request.firstName(), request.lastName(), request.email(), request.password(), Role.CUSTOMER));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return new AuthResponse(jwtService.generateToken(user.getId(), user.getEmail(), user.getRole()),
                "Bearer", user.getId(), user.getEmail(), user.getRole());
    }
}
