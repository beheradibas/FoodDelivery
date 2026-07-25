package com.fooddelivery.mapper;

import com.fooddelivery.dto.review.CreateRatingReviewRequest;
import com.fooddelivery.dto.review.RatingReviewResponse;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.RatingReview;
import com.fooddelivery.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RatingReviewMapper {
    public RatingReview toEntity(CreateRatingReviewRequest request, Order order, User customer) {
        return new RatingReview(order, customer, request.rating(), request.review());
    }

    public RatingReviewResponse toResponse(RatingReview ratingReview) {
        return new RatingReviewResponse(ratingReview.getId(), ratingReview.getOrder().getId(),
                ratingReview.getCustomer().getId(), ratingReview.getRating(), ratingReview.getReview(), ratingReview.getCreatedAt());
    }
}
