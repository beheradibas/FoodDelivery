package com.fooddelivery.service;

import com.fooddelivery.dto.order.OrderPlacementResponse;
import com.fooddelivery.dto.order.PlaceOrderRequest;
import com.fooddelivery.dto.orderitem.OrderItemResponse;
import com.fooddelivery.dto.orderitem.PlaceOrderItemRequest;
import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.OrderItem;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.DuplicateResourceException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.mapper.OrderItemMapper;
import com.fooddelivery.mapper.OrderMapper;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.OrderItemRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.PaymentRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderPlacementService {
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderPlacementService(UserRepository userRepository, RestaurantRepository restaurantRepository,
                                 MenuItemRepository menuItemRepository, PaymentRepository paymentRepository,
                                 OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                                 OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Transactional
    public OrderPlacementResponse placeOrder(PlaceOrderRequest request) {
        User customer = findCustomer(request.customerId());
        Restaurant restaurant = findRestaurant(request.restaurantId());
        validateUniqueMenuItems(request.items());

        Map<Long, PlaceOrderItemRequest> requestedItems = request.items().stream()
                .collect(Collectors.toMap(PlaceOrderItemRequest::menuItemId, Function.identity()));
        List<MenuItem> menuItems = requestedItems.keySet().stream().sorted().map(this::lockMenuItem).toList();
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (MenuItem menuItem : menuItems) {
            PlaceOrderItemRequest itemRequest = requestedItems.get(menuItem.getId());
            validateMenuItem(menuItem, restaurant);
            menuItem.deductStock(itemRequest.quantity());
            total = total.add(menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }

        if (paymentRepository.existsByPaymentReference(request.paymentReference())) {
            throw new DuplicateResourceException("Payment reference already exists: " + request.paymentReference());
        }
        Payment payment = paymentRepository.save(new Payment(customer, request.paymentReference(), total));
        Order order = orderRepository.save(new Order(customer, restaurant, payment));
        for (MenuItem menuItem : menuItems) {
            orderItems.add(new OrderItem(order, menuItem, requestedItems.get(menuItem.getId()).quantity()));
        }
        List<OrderItemResponse> itemResponses = orderItemRepository.saveAll(orderItems).stream()
                .map(orderItemMapper::toResponse).toList();
        return new OrderPlacementResponse(orderMapper.toResponse(order), itemResponses);
    }

    private MenuItem lockMenuItem(Long menuItemId) {
        return menuItemRepository.findByIdForUpdate(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + menuItemId));
    }

    private void validateMenuItem(MenuItem menuItem, Restaurant restaurant) {
        if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
            throw new IllegalArgumentException("Menu item does not belong to restaurant");
        }
        if (!menuItem.isAvailable()) {
            throw new IllegalArgumentException("Menu item is unavailable: " + menuItem.getId());
        }
    }

    private void validateUniqueMenuItems(List<PlaceOrderItemRequest> items) {
        if (new HashSet<>(items.stream().map(PlaceOrderItemRequest::menuItemId).toList()).size() != items.size()) {
            throw new IllegalArgumentException("An order cannot contain duplicate menu items");
        }
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
}
