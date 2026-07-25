package com.fooddelivery.mapper;

import com.fooddelivery.dto.address.CreateCustomerAddressRequest;
import com.fooddelivery.dto.address.CustomerAddressResponse;
import com.fooddelivery.dto.address.UpdateCustomerAddressRequest;
import com.fooddelivery.entity.CustomerAddress;
import com.fooddelivery.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CustomerAddressMapper {
    public CustomerAddress toEntity(CreateCustomerAddressRequest request, User customer) {
        return new CustomerAddress(customer, request.label(), request.addressLine1(), request.addressLine2(),
                request.city(), request.state(), request.postalCode());
    }

    public void updateEntity(CustomerAddress address, UpdateCustomerAddressRequest request) {
        address.update(request.label(), request.addressLine1(), request.addressLine2(), request.city(), request.state(), request.postalCode());
    }

    public CustomerAddressResponse toResponse(CustomerAddress address) {
        return new CustomerAddressResponse(address.getId(), address.getCustomer().getId(), address.getLabel(),
                address.getAddressLine1(), address.getAddressLine2(), address.getCity(), address.getState(), address.getPostalCode());
    }
}
