package com.fooddelivery.dto.deliveryassignment;

import jakarta.validation.constraints.NotNull;

public record DeliveryAssignmentRequest(@NotNull(message = "Delivery partner ID is required") Long deliveryPartnerId) { }
