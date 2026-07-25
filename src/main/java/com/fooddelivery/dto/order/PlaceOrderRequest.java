package com.fooddelivery.dto.order;

import com.fooddelivery.dto.orderitem.PlaceOrderItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlaceOrderRequest(
        @NotNull(message = "Customer ID is required") Long customerId,
        @NotNull(message = "Restaurant ID is required") Long restaurantId,
        @NotBlank(message = "Payment reference is required") @Size(max = 100, message = "Payment reference must not exceed 100 characters") String paymentReference,
        @NotEmpty(message = "At least one order item is required") List<@Valid PlaceOrderItemRequest> items
) { }
