package com.fooddelivery.repository;

import com.fooddelivery.entity.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    boolean existsByUserId(Long userId);
}
