package com.fooddelivery.mapper;

import com.fooddelivery.dto.order.CreateOrderRequest;
import com.fooddelivery.dto.order.OrderResponse;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.User;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public Order toEntity(CreateOrderRequest request, User customer, Restaurant restaurant, Payment payment) {
        return new Order(customer, restaurant, payment);
    }

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(order.getId(), order.getCustomer().getId(), order.getRestaurant().getId(),
                order.getPayment().getId(), order.getStatus(), order.getCreatedAt());
    }
}
