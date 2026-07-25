package com.fooddelivery.dto.deliverypartner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDeliveryPartnerRequest(
        @NotNull(message = "User ID is required") Long userId,
        @NotBlank(message = "City is required") @Size(max = 100, message = "City must not exceed 100 characters") String city
) { }
