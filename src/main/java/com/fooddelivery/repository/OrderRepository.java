package com.fooddelivery.repository;

import com.fooddelivery.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomerId(Long customerId);

    List<Order> findAllByRestaurantId(Long restaurantId);
}
