package com.fooddelivery.service;

import com.fooddelivery.dto.auth.AuthResponse;
import com.fooddelivery.dto.auth.LoginRequest;
import com.fooddelivery.dto.auth.RegisterRequest;
import com.fooddelivery.dto.user.UserResponse;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private UserService userService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @InjectMocks private AuthService authService;

    @Test
    void registrationAlwaysCreatesCustomer() {
        RegisterRequest request = new RegisterRequest("Asha", "Sharma", "asha@example.com", "password123");
        UserResponse response = new UserResponse(1L, "Asha", "Sharma", "asha@example.com", Role.CUSTOMER, null);
        when(userService.createUser(any())).thenReturn(response);

        UserResponse result = authService.register(request);

        assertThat(result.role()).isEqualTo(Role.CUSTOMER);
        verify(userService).createUser(any());
    }

    @Test
    void loginReturnsJwtForValidCredentials() {
        User user = mock(User.class);
        String email = "asha@example.com";
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn(email);
        when(user.getPassword()).thenReturn("encoded");
        when(user.getRole()).thenReturn(Role.CUSTOMER);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(true);
        when(jwtService.generateToken(1L, email, Role.CUSTOMER)).thenReturn("jwt-token");

        AuthResponse response = authService.login(new LoginRequest("asha@example.com", "password123"));

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.role()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void loginRejectsInvalidCredentials() {
        User user = mock(User.class);
        when(user.getPassword()).thenReturn("encoded");
        when(userRepository.findByEmail("asha@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("asha@example.com", "wrong")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");
    }
}
