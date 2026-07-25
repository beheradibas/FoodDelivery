package com.fooddelivery.dto.user;

import com.fooddelivery.entity.Role;

import java.time.Instant;

public record UserResponse(Long id, String firstName, String lastName, String email, Role role, Instant createdAt) {
}
