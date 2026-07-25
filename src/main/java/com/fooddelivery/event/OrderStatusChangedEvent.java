package com.fooddelivery.event;

import com.fooddelivery.entity.OrderStatus;

public record OrderStatusChangedEvent(Long orderId, Long customerId, Long restaurantId, Long deliveryPartnerId,
                                      OrderStatus previousStatus, OrderStatus currentStatus) {
}
