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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

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

    @Mock
    private ApplicationEventPublisher eventPublisher;

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

    @Test
    void updateOrderStatusRejectsSkippedTransition() {
        User customer = customer(1L);
        Order order = new Order(customer, restaurant(10L), payment(customer, 100L));
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.PREPARING)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid order status transition from PLACED to PREPARING");
    }

    @Test
    void updateOrderStatusAllowsOnlyTheCompleteLifecycleInOrder() {
        User customer = customer(1L);
        Order order = new Order(customer, restaurant(10L), payment(customer, 100L));
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));

        orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.ACCEPTED));
        orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.PREPARING));
        orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY));
        OrderResponse response = orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.DELIVERED));

        assertThat(response.status()).isEqualTo(OrderStatus.DELIVERED);
        assertThatThrownBy(() -> orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.ACCEPTED)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PLACED", "ACCEPTED", "PREPARING"})
    void everyActiveStateCanTransitionToRejected(OrderStatus currentStatus) {
        User customer = customer(1L);
        Order order = new Order(customer, restaurant(10L), payment(customer, 100L));
        advanceTo(order, currentStatus);
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.REJECTED));

        assertThat(response.status()).isEqualTo(OrderStatus.REJECTED);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PLACED", "ACCEPTED", "PREPARING"})
    void everyCancellableStateCanTransitionToCancelled(OrderStatus currentStatus) {
        User customer = customer(1L);
        Order order = new Order(customer, restaurant(10L), payment(customer, 100L));
        advanceTo(order, currentStatus);
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.CANCELLED));

        assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void rejectedIsTerminal() {
        User customer = customer(1L);
        Order order = new Order(customer, restaurant(10L), payment(customer, 100L));
        order.updateStatus(OrderStatus.REJECTED);
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.ACCEPTED)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deliveryStageCannotBeRejectedOrCancelled() {
        User customer = customer(1L);
        Order order = new Order(customer, restaurant(10L), payment(customer, 100L));
        advanceTo(order, OrderStatus.OUT_FOR_DELIVERY);
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.REJECTED)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> orderService.updateOrderStatus(7L, new UpdateOrderStatusRequest(OrderStatus.CANCELLED)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void advanceTo(Order order, OrderStatus target) {
        if (target == OrderStatus.ACCEPTED) order.updateStatus(OrderStatus.ACCEPTED);
        if (target == OrderStatus.PREPARING) {
            order.updateStatus(OrderStatus.ACCEPTED);
            order.updateStatus(OrderStatus.PREPARING);
        }
        if (target == OrderStatus.OUT_FOR_DELIVERY) {
            order.updateStatus(OrderStatus.ACCEPTED);
            order.updateStatus(OrderStatus.PREPARING);
            order.updateStatus(OrderStatus.OUT_FOR_DELIVERY);
        }
        if (target == OrderStatus.DELIVERED) {
            order.updateStatus(OrderStatus.ACCEPTED);
            order.updateStatus(OrderStatus.PREPARING);
            order.updateStatus(OrderStatus.OUT_FOR_DELIVERY);
            order.updateStatus(OrderStatus.DELIVERED);
        }
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
