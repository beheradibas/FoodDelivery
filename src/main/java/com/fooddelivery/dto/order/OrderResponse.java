package com.fooddelivery.dto.order;

import com.fooddelivery.entity.OrderStatus;

import java.time.Instant;

public record OrderResponse(Long id, Long customerId, Long restaurantId, Long paymentId, OrderStatus status, Instant createdAt) { }
