package com.fooddelivery.repository;

import com.fooddelivery.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrderId(Long orderId);

    Optional<OrderItem> findByIdAndOrderId(Long orderItemId, Long orderId);

    boolean existsByOrderIdAndMenuItemId(Long orderId, Long menuItemId);
}
