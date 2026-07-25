package com.fooddelivery.repository;

import com.fooddelivery.entity.RatingReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingReviewRepository extends JpaRepository<RatingReview, Long> {
    boolean existsByOrderIdAndCustomerId(Long orderId, Long customerId);
}
