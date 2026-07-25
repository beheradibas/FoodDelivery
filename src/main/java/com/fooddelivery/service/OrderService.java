package com.fooddelivery.service;

import com.fooddelivery.dto.order.CreateOrderRequest;
import com.fooddelivery.dto.order.OrderResponse;
import com.fooddelivery.dto.order.UpdateOrderStatusRequest;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.mapper.OrderMapper;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.PaymentRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final PaymentRepository paymentRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, RestaurantRepository restaurantRepository,
                        PaymentRepository paymentRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.paymentRepository = paymentRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User customer = findCustomer(request.customerId());
        Restaurant restaurant = findRestaurant(request.restaurantId());
        Payment payment = findPaymentForCustomer(request.paymentId(), request.customerId());
        return orderMapper.toResponse(orderRepository.save(orderMapper.toEntity(request, customer, restaurant, payment)));
    }

    public OrderResponse getOrder(Long orderId) {
        return orderMapper.toResponse(findOrder(orderId));
    }

    public List<OrderResponse> getCustomerOrders(Long customerId) {
        findCustomer(customerId);
        return orderRepository.findAllByCustomerId(customerId).stream().map(orderMapper::toResponse).toList();
    }

    public List<OrderResponse> getRestaurantOrders(Long restaurantId) {
        findRestaurant(restaurantId);
        return orderRepository.findAllByRestaurantId(restaurantId).stream().map(orderMapper::toResponse).toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = findOrder(orderId);
        order.updateStatus(request.status());
        return orderMapper.toResponse(order);
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    private User findCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        if (customer.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("User is not a customer: " + customerId);
        }
        return customer;
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + restaurantId));
    }

    private Payment findPaymentForCustomer(Long paymentId, Long customerId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        if (!payment.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Payment does not belong to customer: " + customerId);
        }
        return payment;
    }
}
