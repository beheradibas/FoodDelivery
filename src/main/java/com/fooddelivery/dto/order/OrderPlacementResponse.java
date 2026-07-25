package com.fooddelivery.dto.order;

import com.fooddelivery.dto.orderitem.OrderItemResponse;

import java.util.List;

public record OrderPlacementResponse(OrderResponse order, List<OrderItemResponse> items) { }
