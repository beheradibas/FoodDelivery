package com.fooddelivery.event;

import com.fooddelivery.service.NotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DeliveryPartnerOrderNotificationListener {
    private final NotificationService notificationService;

    public DeliveryPartnerOrderNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async("notificationTaskExecutor")
    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        if (event.deliveryPartnerId() != null) {
            notificationService.notifyDeliveryPartner(event.deliveryPartnerId(), event.orderId(), event.currentStatus());
        }
    }
}
