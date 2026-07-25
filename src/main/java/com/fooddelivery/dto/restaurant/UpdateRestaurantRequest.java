package com.fooddelivery.dto.restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantRequest(
        @NotBlank(message = "Restaurant name is required") @Size(max = 100, message = "Restaurant name must not exceed 100 characters") String name,
        @NotBlank(message = "City is required") @Size(max = 100, message = "City must not exceed 100 characters") String city,
        @NotBlank(message = "Address is required") @Size(max = 255, message = "Address must not exceed 255 characters") String address,
        @NotNull(message = "Owner ID is required") Long ownerId
) {
}
