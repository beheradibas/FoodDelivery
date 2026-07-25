package com.fooddelivery.dto.orderitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlaceOrderItemRequest(
        @NotNull(message = "Menu item ID is required") Long menuItemId,
        @NotNull(message = "Quantity is required") @Positive(message = "Quantity must be greater than zero") Integer quantity
) { }
