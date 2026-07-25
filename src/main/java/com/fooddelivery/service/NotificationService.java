package com.fooddelivery.service;

import com.fooddelivery.entity.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void notifyCustomer(Long customerId, Long orderId, OrderStatus status) {
        log.info("Order {} changed to {}; notifying customer {}", orderId, status, customerId);
    }

    public void notifyRestaurant(Long restaurantId, Long orderId, OrderStatus status) {
        log.info("Order {} changed to {}; notifying restaurant {}", orderId, status, restaurantId);
    }

    public void notifyDeliveryPartner(Long deliveryPartnerId, Long orderId, OrderStatus status) {
        log.info("Order {} changed to {}; notifying delivery partner {}", orderId, status, deliveryPartnerId);
    }
}
