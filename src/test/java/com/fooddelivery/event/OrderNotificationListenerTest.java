package com.fooddelivery.event;

import com.fooddelivery.entity.OrderStatus;
import com.fooddelivery.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderNotificationListenerTest {
    @Mock
    private NotificationService notificationService;

    @Test
    void customerListenerNotifiesCustomer() {
        new CustomerOrderNotificationListener(notificationService).onOrderStatusChanged(event());
        verify(notificationService).notifyCustomer(1L, 10L, OrderStatus.ACCEPTED);
    }

    @Test
    void restaurantListenerNotifiesRestaurant() {
        new RestaurantOrderNotificationListener(notificationService).onOrderStatusChanged(event());
        verify(notificationService).notifyRestaurant(2L, 10L, OrderStatus.ACCEPTED);
    }

    @Test
    void partnerListenerNotifiesAssignedPartner() {
        new DeliveryPartnerOrderNotificationListener(notificationService).onOrderStatusChanged(event());
        verify(notificationService).notifyDeliveryPartner(3L, 10L, OrderStatus.ACCEPTED);
    }

    @Test
    void partnerListenerSkipsUnassignedOrder() {
        new DeliveryPartnerOrderNotificationListener(notificationService).onOrderStatusChanged(
                new OrderStatusChangedEvent(10L, 1L, 2L, null, OrderStatus.PLACED, OrderStatus.ACCEPTED));
        org.mockito.Mockito.verifyNoInteractions(notificationService);
    }

    private OrderStatusChangedEvent event() {
        return new OrderStatusChangedEvent(10L, 1L, 2L, 3L, OrderStatus.PLACED, OrderStatus.ACCEPTED);
    }
}
