package com.fooddelivery.repository;

import com.fooddelivery.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findAllByRestaurantId(Long restaurantId);
    List<MenuItem> findAllByRestaurantIdAndAvailableTrue(Long restaurantId);
    Optional<MenuItem> findByIdAndRestaurantId(Long menuItemId, Long restaurantId);
    Optional<MenuItem> findByIdAndRestaurantIdAndAvailableTrue(Long menuItemId, Long restaurantId);
}
