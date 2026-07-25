package com.fooddelivery.repository;

import com.fooddelivery.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select item from MenuItem item where item.id = :id")
    Optional<MenuItem> findByIdForUpdate(@Param("id") Long id);

    List<MenuItem> findAllByRestaurantId(Long restaurantId);
    List<MenuItem> findAllByRestaurantIdAndAvailableTrue(Long restaurantId);
    Optional<MenuItem> findByIdAndRestaurantId(Long menuItemId, Long restaurantId);
    Optional<MenuItem> findByIdAndRestaurantIdAndAvailableTrue(Long menuItemId, Long restaurantId);
}
