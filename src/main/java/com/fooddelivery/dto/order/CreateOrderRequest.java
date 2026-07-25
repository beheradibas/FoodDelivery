package com.fooddelivery.dto.order;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull(message = "Customer ID is required") Long customerId,
        @NotNull(message = "Restaurant ID is required") Long restaurantId,
        @NotNull(message = "Payment ID is required") Long paymentId
) { }
