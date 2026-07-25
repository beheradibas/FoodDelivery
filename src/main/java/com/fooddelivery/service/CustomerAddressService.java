package com.fooddelivery.service;

import com.fooddelivery.dto.address.CreateCustomerAddressRequest;
import com.fooddelivery.dto.address.CustomerAddressResponse;
import com.fooddelivery.dto.address.UpdateCustomerAddressRequest;
import com.fooddelivery.entity.CustomerAddress;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.mapper.CustomerAddressMapper;
import com.fooddelivery.repository.CustomerAddressRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerAddressService {
    private final CustomerAddressRepository customerAddressRepository;
    private final UserRepository userRepository;
    private final CustomerAddressMapper customerAddressMapper;

    public CustomerAddressService(CustomerAddressRepository customerAddressRepository, UserRepository userRepository,
                                  CustomerAddressMapper customerAddressMapper) {
        this.customerAddressRepository = customerAddressRepository;
        this.userRepository = userRepository;
        this.customerAddressMapper = customerAddressMapper;
    }

    @Transactional
    public CustomerAddressResponse createAddress(Long customerId, CreateCustomerAddressRequest request) {
        User customer = findCustomer(customerId);
        return customerAddressMapper.toResponse(customerAddressRepository.save(customerAddressMapper.toEntity(request, customer)));
    }

    public List<CustomerAddressResponse> getAddresses(Long customerId) {
        findCustomer(customerId);
        return customerAddressRepository.findAllByCustomerId(customerId).stream().map(customerAddressMapper::toResponse).toList();
    }

    public CustomerAddressResponse getAddress(Long customerId, Long addressId) {
        return customerAddressMapper.toResponse(findAddress(customerId, addressId));
    }

    @Transactional
    public CustomerAddressResponse updateAddress(Long customerId, Long addressId, UpdateCustomerAddressRequest request) {
        CustomerAddress address = findAddress(customerId, addressId);
        customerAddressMapper.updateEntity(address, request);
        return customerAddressMapper.toResponse(address);
    }

    @Transactional
    public void deleteAddress(Long customerId, Long addressId) {
        customerAddressRepository.delete(findAddress(customerId, addressId));
    }

    private User findCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        if (customer.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("User is not a customer: " + customerId);
        }
        return customer;
    }

    private CustomerAddress findAddress(Long customerId, Long addressId) {
        findCustomer(customerId);
        return customerAddressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer address not found: " + addressId));
    }
}
