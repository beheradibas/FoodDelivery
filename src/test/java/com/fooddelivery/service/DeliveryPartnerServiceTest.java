package com.fooddelivery.service;

import com.fooddelivery.dto.deliverypartner.CreateDeliveryPartnerRequest;
import com.fooddelivery.dto.deliverypartner.DeliveryPartnerResponse;
import com.fooddelivery.entity.DeliveryPartner;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.DuplicateResourceException;
import com.fooddelivery.mapper.DeliveryPartnerMapper;
import com.fooddelivery.repository.DeliveryPartnerRepository;
import com.fooddelivery.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryPartnerServiceTest {
    @Mock
    private DeliveryPartnerRepository deliveryPartnerRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private DeliveryPartnerMapper deliveryPartnerMapper;

    @InjectMocks
    private DeliveryPartnerService deliveryPartnerService;

    @Test
    void createDeliveryPartnerCreatesProfileForDeliveryPartnerUser() {
        User user = deliveryPartnerUser(1L);
        CreateDeliveryPartnerRequest request = new CreateDeliveryPartnerRequest(1L, "Pune");
        when(deliveryPartnerRepository.existsByUserId(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(deliveryPartnerRepository.save(any(DeliveryPartner.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeliveryPartnerResponse response = deliveryPartnerService.createDeliveryPartner(request);

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.city()).isEqualTo("Pune");
    }

    @Test
    void createDeliveryPartnerRejectsExistingProfile() {
        CreateDeliveryPartnerRequest request = new CreateDeliveryPartnerRequest(1L, "Pune");
        when(deliveryPartnerRepository.existsByUserId(1L)).thenReturn(true);

        assertThatThrownBy(() -> deliveryPartnerService.createDeliveryPartner(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Delivery partner profile already exists for user: 1");
    }

    @Test
    void createDeliveryPartnerRejectsNonDeliveryPartnerUser() {
        User user = mock(User.class);
        when(user.getRole()).thenReturn(Role.CUSTOMER);
        when(deliveryPartnerRepository.existsByUserId(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        CreateDeliveryPartnerRequest request = new CreateDeliveryPartnerRequest(1L, "Pune");

        assertThatThrownBy(() -> deliveryPartnerService.createDeliveryPartner(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User is not a delivery partner: 1");
    }

    private User deliveryPartnerUser(Long id) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getRole()).thenReturn(Role.DELIVERY_PARTNER);
        return user;
    }
}
