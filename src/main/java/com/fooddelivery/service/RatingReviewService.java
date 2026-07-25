package com.fooddelivery.service;

import com.fooddelivery.dto.review.CreateRatingReviewRequest;
import com.fooddelivery.dto.review.RatingReviewResponse;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.OrderStatus;
import com.fooddelivery.entity.RatingReview;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.DuplicateResourceException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.mapper.RatingReviewMapper;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.RatingReviewRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RatingReviewService {
    private final RatingReviewRepository ratingReviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RatingReviewMapper ratingReviewMapper;

    public RatingReviewService(RatingReviewRepository ratingReviewRepository, OrderRepository orderRepository,
                               UserRepository userRepository, RatingReviewMapper ratingReviewMapper) {
        this.ratingReviewRepository = ratingReviewRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.ratingReviewMapper = ratingReviewMapper;
    }

    @Transactional
    public RatingReviewResponse createReview(CreateRatingReviewRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.orderId()));
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Only delivered orders can be rated");
        }
        User customer = userRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.customerId()));
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Customer does not own order: " + request.orderId());
        }
        if (ratingReviewRepository.existsByOrderIdAndCustomerId(request.orderId(), request.customerId())) {
            throw new DuplicateResourceException("Order has already been reviewed: " + request.orderId());
        }
        RatingReview ratingReview = ratingReviewRepository.save(ratingReviewMapper.toEntity(request, order, customer));
        return ratingReviewMapper.toResponse(ratingReview);
    }

    public RatingReviewResponse getReview(Long reviewId) {
        RatingReview ratingReview = ratingReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating review not found: " + reviewId));
        return ratingReviewMapper.toResponse(ratingReview);
    }
}
