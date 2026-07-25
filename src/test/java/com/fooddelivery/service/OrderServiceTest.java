package com.fooddelivery.service;

import com.fooddelivery.dto.order.CreateOrderRequest;
import com.fooddelivery.dto.order.OrderResponse;
import com.fooddelivery.dto.order.UpdateOrderStatusRequest;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.OrderStatus;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.mapper.OrderMapper;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Spy
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderLinksCustomerRestaurantAndPayment() {
        User customer = customer(1L);
        Restaurant restaurant = restaurant(10L);
        Payment payment = payment(customer, 100L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(paymentRepository.findById(100L)).thenReturn(Optional.of(payment));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.createOrder(new CreateOrderRequest(1L, 10L, 100L));

        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.restaurantId()).isEqualTo(10L);
        assertThat(response.paymentId()).isEqualTo(100L);
        assertThat(response.status()).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    void createOrderRejectsPaymentForAnotherCustomer() {
        User customer = customer(1L);
        Restaurant restaurant = restaurant(10L);
        Payment payment = payment(customer(2L), 100L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(paymentRepository.findById(100L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> orderService.createOrder(new CreateOrderRequest(1L, 10L, 100L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment does not belong to customer: 1");
    }

    @Test
    void updateOrderStatusChangesOrderStatus() {
        User customer = customer(1L);
        Order order = new Order(customer, restaurant(10L), payment(customer, 100L));
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.ACCEPTED));

        assertThat(response.status()).isEqualTo(OrderStatus.ACCEPTED);
    }

    private User customer(Long id) {
        User customer = mock(User.class);
        lenient().when(customer.getId()).thenReturn(id);
        lenient().when(customer.getRole()).thenReturn(Role.CUSTOMER);
        return customer;
    }

    private Restaurant restaurant(Long id) {
        Restaurant restaurant = mock(Restaurant.class);
        lenient().when(restaurant.getId()).thenReturn(id);
        return restaurant;
    }

    private Payment payment(User customer, Long id) {
        Payment payment = mock(Payment.class);
        lenient().when(payment.getId()).thenReturn(id);
        lenient().when(payment.getCustomer()).thenReturn(customer);
        return payment;
    }
}
