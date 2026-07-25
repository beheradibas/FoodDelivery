package com.fooddelivery.dto.auth;

import com.fooddelivery.entity.Role;

public record AuthResponse(String accessToken, String tokenType, Long userId, String email, Role role) { }
