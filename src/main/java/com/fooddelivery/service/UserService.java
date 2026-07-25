package com.fooddelivery.service;

import com.fooddelivery.dto.user.CreateUserRequest;
import com.fooddelivery.dto.user.UpdateUserRequest;
import com.fooddelivery.dto.user.UserResponse;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.DuplicateResourceException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        ensureEmailIsAvailable(request.email());
        User user = new User(request.firstName(), request.lastName(), request.email(), passwordEncoder.encode(request.password()), request.role());
        return toResponse(userRepository.save(user));
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse getUser(Long id) {
        return toResponse(findUser(id));
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUser(id);
        if (!user.getEmail().equalsIgnoreCase(request.email())) {
            ensureEmailIsAvailable(request.email());
        }
        user.update(request.firstName(), request.lastName(), request.email(), passwordEncoder.encode(request.password()), request.role());
        return toResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(findUser(id));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private void ensureEmailIsAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already in use");
        }
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
