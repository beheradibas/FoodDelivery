package com.fooddelivery.dto.review;

import java.time.Instant;

public record RatingReviewResponse(Long id, Long orderId, Long customerId, Integer rating, String review, Instant createdAt) { }
