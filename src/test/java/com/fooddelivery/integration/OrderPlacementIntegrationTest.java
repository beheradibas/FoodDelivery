package com.fooddelivery.integration;

import com.fooddelivery.dto.order.OrderPlacementResponse;
import com.fooddelivery.dto.order.PlaceOrderRequest;
import com.fooddelivery.dto.orderitem.PlaceOrderItemRequest;
import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.OrderItemRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.PaymentRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.OrderPlacementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderPlacementIntegrationTest extends AbstractIntegrationTest {
    @Autowired private OrderPlacementService orderPlacementService;
    @Autowired private UserRepository userRepository;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private MenuItemRepository menuItemRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;

    @Test
    void placementPersistsPaymentOrderItemsAndStockDeduction() {
        User customer = userRepository.save(new User("Asha", "Sharma", "integration@example.com", "encoded", Role.CUSTOMER));
        User owner = userRepository.save(new User("Owner", "One", "owner-integration@example.com", "encoded", Role.RESTAURANT_OWNER));
        Restaurant restaurant = restaurantRepository.save(new Restaurant("Test Kitchen", "Pune", "1 Main Street", owner));
        MenuItem menuItem = menuItemRepository.save(new MenuItem(restaurant, "Thali", "Meal", new BigDecimal("100.00"), 5, true));

        OrderPlacementResponse response = orderPlacementService.placeOrder(new PlaceOrderRequest(
                customer.getId(), restaurant.getId(), "INTEGRATION-PAY-1",
                List.of(new PlaceOrderItemRequest(menuItem.getId(), 2))));

        MenuItem storedItem = menuItemRepository.findById(menuItem.getId()).orElseThrow();
        assertThat(response.order().status().name()).isEqualTo("PLACED");
        assertThat(paymentRepository.count()).isEqualTo(1);
        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(orderItemRepository.count()).isEqualTo(1);
        assertThat(storedItem.getStockQuantity()).isEqualTo(3);
    }

    @Test
    void failedPlacementRollsBackPaymentOrderItemsAndStock() {
        User customer = userRepository.save(new User("Rollback", "Customer", "rollback@example.com", "encoded", Role.CUSTOMER));
        User owner = userRepository.save(new User("Rollback", "Owner", "rollback-owner@example.com", "encoded", Role.RESTAURANT_OWNER));
        Restaurant restaurant = restaurantRepository.save(new Restaurant("Rollback Kitchen", "Pune", "2 Main Street", owner));
        MenuItem menuItem = menuItemRepository.save(new MenuItem(restaurant, "Limited Meal", "Meal", new BigDecimal("50.00"), 1, true));
        long paymentsBefore = paymentRepository.count();
        long ordersBefore = orderRepository.count();
        long itemsBefore = orderItemRepository.count();

        assertThatThrownBy(() -> orderPlacementService.placeOrder(new PlaceOrderRequest(
                customer.getId(), restaurant.getId(), "INTEGRATION-PAY-ROLLBACK",
                List.of(new PlaceOrderItemRequest(menuItem.getId(), 2)))))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(paymentRepository.count()).isEqualTo(paymentsBefore);
        assertThat(orderRepository.count()).isEqualTo(ordersBefore);
        assertThat(orderItemRepository.count()).isEqualTo(itemsBefore);
        assertThat(menuItemRepository.findById(menuItem.getId()).orElseThrow().getStockQuantity()).isEqualTo(1);
    }
}
