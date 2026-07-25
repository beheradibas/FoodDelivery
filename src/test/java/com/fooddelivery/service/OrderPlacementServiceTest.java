package com.fooddelivery.service;

import com.fooddelivery.dto.order.OrderPlacementResponse;
import com.fooddelivery.dto.order.PlaceOrderRequest;
import com.fooddelivery.dto.orderitem.PlaceOrderItemRequest;
import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.mapper.OrderItemMapper;
import com.fooddelivery.mapper.OrderMapper;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.OrderItemRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.PaymentRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderPlacementServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private MenuItemRepository menuItemRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Spy private OrderMapper orderMapper;
    @Spy private OrderItemMapper orderItemMapper;
    @InjectMocks private OrderPlacementService orderPlacementService;

    @Test
    void placeOrderDeductsStockAndCreatesPaymentOrderAndItems() {
        User customer = user(1L, Role.CUSTOMER);
        Restaurant restaurant = restaurant(10L);
        MenuItem menuItem = menuItem(20L, restaurant, 5);
        Payment payment = new Payment(customer, "PAY-1", new BigDecimal("20.00"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(menuItem));
        when(paymentRepository.existsByPaymentReference("PAY-1")).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrderPlacementResponse response = orderPlacementService.placeOrder(
                new PlaceOrderRequest(1L, 10L, "PAY-1", List.of(new PlaceOrderItemRequest(20L, 2))));

        assertThat(menuItem.getStockQuantity()).isEqualTo(3);
        assertThat(response.items()).hasSize(1);
        verify(paymentRepository).save(any(Payment.class));
        verify(orderRepository).save(any());
        verify(orderItemRepository).saveAll(any());
    }

    @Test
    void insufficientStockPreventsPaymentAndOrderCreation() {
        User customer = user(1L, Role.CUSTOMER);
        Restaurant restaurant = restaurant(10L);
        MenuItem menuItem = menuItem(20L, restaurant, 1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(menuItem));

        assertThatThrownBy(() -> orderPlacementService.placeOrder(
                new PlaceOrderRequest(1L, 10L, "PAY-2", List.of(new PlaceOrderItemRequest(20L, 2)))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient stock for menu item: 20");

        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    private User user(Long id, Role role) {
        User user = org.mockito.Mockito.mock(User.class);
        lenient().when(user.getId()).thenReturn(id);
        when(user.getRole()).thenReturn(role);
        return user;
    }

    private Restaurant restaurant(Long id) {
        Restaurant restaurant = org.mockito.Mockito.mock(Restaurant.class);
        lenient().when(restaurant.getId()).thenReturn(id);
        return restaurant;
    }

    private MenuItem menuItem(Long id, Restaurant restaurant, int stock) {
        MenuItem item = new MenuItem(restaurant, "Pizza", "", new BigDecimal("10.00"), stock, true);
        ReflectionTestUtils.setField(item, "id", id);
        return item;
    }
}
