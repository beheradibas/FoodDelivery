package com.fooddelivery.event;

import com.fooddelivery.service.NotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerOrderNotificationListener {
    private final NotificationService notificationService;

    public CustomerOrderNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async("notificationTaskExecutor")
    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        notificationService.notifyCustomer(event.customerId(), event.orderId(), event.currentStatus());
    }
}
