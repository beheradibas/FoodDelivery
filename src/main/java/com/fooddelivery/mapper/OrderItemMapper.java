package com.fooddelivery.mapper;

import com.fooddelivery.dto.orderitem.OrderItemResponse;
import com.fooddelivery.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {
    public OrderItemResponse toResponse(OrderItem orderItem) {
        return new OrderItemResponse(orderItem.getId(), orderItem.getOrder().getId(), orderItem.getMenuItem().getId(),
                orderItem.getMenuItem().getName(), orderItem.getQuantity(), orderItem.getUnitPrice());
    }
}
