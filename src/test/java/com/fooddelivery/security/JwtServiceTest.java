package com.fooddelivery.security;

import com.fooddelivery.entity.Role;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    private static final String SECRET = Base64.getEncoder().encodeToString(
            "ThisIsASufficientlyLongSecretKeyForTests".getBytes());

    @Test
    void generatedTokenCanBeValidatedAndDecoded() {
        JwtService jwtService = new JwtService(SECRET, 3_600_000L);

        String token = jwtService.generateToken(7L, "customer@example.com", Role.CUSTOMER);

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("customer@example.com");
    }

    @Test
    void malformedTokenIsInvalid() {
        JwtService jwtService = new JwtService(SECRET, 3_600_000L);

        assertThat(jwtService.isValid("not-a-jwt")).isFalse();
    }
}
