package com.fooddelivery.dto.deliverypartner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDeliveryPartnerRequest(
        @NotBlank(message = "City is required") @Size(max = 100, message = "City must not exceed 100 characters") String city
) { }
