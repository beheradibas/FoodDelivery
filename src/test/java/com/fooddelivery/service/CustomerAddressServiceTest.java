package com.fooddelivery.service;

import com.fooddelivery.dto.address.CreateCustomerAddressRequest;
import com.fooddelivery.dto.address.CustomerAddressResponse;
import com.fooddelivery.entity.CustomerAddress;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.mapper.CustomerAddressMapper;
import com.fooddelivery.repository.CustomerAddressRepository;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAddressServiceTest {
    @Mock
    private CustomerAddressRepository customerAddressRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private CustomerAddressMapper customerAddressMapper;

    @InjectMocks
    private CustomerAddressService customerAddressService;

    @Test
    void createAddressAssignsItToCustomer() {
        User customer = customer(1L);
        CreateCustomerAddressRequest request = new CreateCustomerAddressRequest(
                "Home", "12 Market Road", null, "Pune", "Maharashtra", "411001");
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerAddressRepository.save(any(CustomerAddress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerAddressResponse response = customerAddressService.createAddress(1L, request);

        verify(customerAddressRepository).save(any(CustomerAddress.class));
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.city()).isEqualTo("Pune");
    }

    @Test
    void createAddressRejectsNonCustomerUser() {
        User owner = mock(User.class);
        when(owner.getRole()).thenReturn(Role.RESTAURANT_OWNER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        CreateCustomerAddressRequest request = new CreateCustomerAddressRequest(
                "Home", "12 Market Road", null, "Pune", "Maharashtra", "411001");

        assertThatThrownBy(() -> customerAddressService.createAddress(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User is not a customer: 1");
    }

    @Test
    void getAddressRejectsAddressOutsideCustomerScope() {
        User customer = customer(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerAddressRepository.findByIdAndCustomerId(7L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerAddressService.getAddress(1L, 7L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer address not found: 7");
    }

    private User customer(Long id) {
        User customer = mock(User.class);
        lenient().when(customer.getId()).thenReturn(id);
        when(customer.getRole()).thenReturn(Role.CUSTOMER);
        return customer;
    }
}
