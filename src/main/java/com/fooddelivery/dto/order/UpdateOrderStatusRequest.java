package com.fooddelivery.dto.order;

import com.fooddelivery.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull(message = "Order status is required") OrderStatus status) { }
