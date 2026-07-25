package com.fooddelivery.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRatingReviewRequest(
        @NotNull(message = "Order ID is required") Long orderId,
        @NotNull(message = "Customer ID is required") Long customerId,
        @NotNull(message = "Rating is required") @Min(value = 1, message = "Rating must be between 1 and 5") @Max(value = 5, message = "Rating must be between 1 and 5") Integer rating,
        @Size(max = 1000, message = "Review must not exceed 1000 characters") String review
) { }
