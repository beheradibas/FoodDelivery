package com.fooddelivery.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerAddressRequest(
        @NotBlank(message = "Address label is required") @Size(max = 100, message = "Address label must not exceed 100 characters") String label,
        @NotBlank(message = "Address line 1 is required") @Size(max = 255, message = "Address line 1 must not exceed 255 characters") String addressLine1,
        @Size(max = 255, message = "Address line 2 must not exceed 255 characters") String addressLine2,
        @NotBlank(message = "City is required") @Size(max = 100, message = "City must not exceed 100 characters") String city,
        @NotBlank(message = "State is required") @Size(max = 100, message = "State must not exceed 100 characters") String state,
        @NotBlank(message = "Postal code is required") @Size(max = 20, message = "Postal code must not exceed 20 characters") String postalCode
) { }
