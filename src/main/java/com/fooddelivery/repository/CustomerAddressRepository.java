package com.fooddelivery.repository;

import com.fooddelivery.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
    List<CustomerAddress> findAllByCustomerId(Long customerId);

    Optional<CustomerAddress> findByIdAndCustomerId(Long addressId, Long customerId);
}
