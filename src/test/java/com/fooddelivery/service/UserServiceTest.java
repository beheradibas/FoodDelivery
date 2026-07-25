package com.fooddelivery.service;

import com.fooddelivery.dto.user.CreateUserRequest;
import com.fooddelivery.dto.user.UpdateUserRequest;
import com.fooddelivery.dto.user.UserResponse;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.DuplicateResourceException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CreateUserRequest("Asha", "Sharma", "asha@example.com", "password123", Role.CUSTOMER);
    }

    @Test
    void createUserSavesAnEncodedPassword() {
        when(userRepository.existsByEmail(createRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(createRequest.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.createUser(createRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
        assertThat(response.email()).isEqualTo(createRequest.email());
        assertThat(response.role()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void createUserRejectsDuplicateEmail() {
        when(userRepository.existsByEmail(createRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email is already in use");

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUserSupportsRestaurantOwnerRole() {
        CreateUserRequest ownerRequest = new CreateUserRequest(
                "Ravi", "Kumar", "ravi@example.com", "password123", Role.RESTAURANT_OWNER);
        when(userRepository.existsByEmail(ownerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(ownerRequest.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.createUser(ownerRequest);

        assertThat(response.role()).isEqualTo(Role.RESTAURANT_OWNER);
    }

    @Test
    void getUserThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found: 99");
    }

    @Test
    void updateUserUpdatesUserDetailsAndPassword() {
        User user = new User("Asha", "Sharma", "asha@example.com", "old-password", Role.CUSTOMER);
        UpdateUserRequest updateRequest = new UpdateUserRequest("Asha", "Patel", "asha@example.com", "newpassword123", Role.ADMIN);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updateRequest.password())).thenReturn("new-encoded-password");

        UserResponse response = userService.updateUser(1L, updateRequest);

        assertThat(user.getLastName()).isEqualTo("Patel");
        assertThat(user.getPassword()).isEqualTo("new-encoded-password");
        assertThat(response.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void deleteUserDeletesExistingUser() {
        User user = new User("Asha", "Sharma", "asha@example.com", "password", Role.CUSTOMER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }
}
