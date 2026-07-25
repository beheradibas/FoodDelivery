package com.fooddelivery.controller;

import com.fooddelivery.dto.address.CreateCustomerAddressRequest;
import com.fooddelivery.dto.address.CustomerAddressResponse;
import com.fooddelivery.dto.address.UpdateCustomerAddressRequest;
import com.fooddelivery.service.CustomerAddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/addresses")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerAddressController {
    private final CustomerAddressService customerAddressService;

    public CustomerAddressController(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }

    @PostMapping
    public ResponseEntity<CustomerAddressResponse> createAddress(@PathVariable Long customerId,
                                                                  @Valid @RequestBody CreateCustomerAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerAddressService.createAddress(customerId, request));
    }

    @GetMapping
    public ResponseEntity<List<CustomerAddressResponse>> getAddresses(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerAddressService.getAddresses(customerId));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<CustomerAddressResponse> getAddress(@PathVariable Long customerId, @PathVariable Long addressId) {
        return ResponseEntity.ok(customerAddressService.getAddress(customerId, addressId));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<CustomerAddressResponse> updateAddress(@PathVariable Long customerId, @PathVariable Long addressId,
                                                                  @Valid @RequestBody UpdateCustomerAddressRequest request) {
        return ResponseEntity.ok(customerAddressService.updateAddress(customerId, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long customerId, @PathVariable Long addressId) {
        customerAddressService.deleteAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }
}
