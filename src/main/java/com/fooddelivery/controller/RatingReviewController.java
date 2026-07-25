package com.fooddelivery.controller;

import com.fooddelivery.dto.review.CreateRatingReviewRequest;
import com.fooddelivery.dto.review.RatingReviewResponse;
import com.fooddelivery.service.RatingReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/reviews")
public class RatingReviewController {
    private final RatingReviewService ratingReviewService;

    public RatingReviewController(RatingReviewService ratingReviewService) {
        this.ratingReviewService = ratingReviewService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RatingReviewResponse> createReview(@Valid @RequestBody CreateRatingReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingReviewService.createReview(request));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<RatingReviewResponse> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(ratingReviewService.getReview(reviewId));
    }
}
