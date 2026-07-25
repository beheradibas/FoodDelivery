package com.fooddelivery.service;

import com.fooddelivery.dto.review.CreateRatingReviewRequest;
import com.fooddelivery.dto.review.RatingReviewResponse;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.OrderStatus;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.RatingReview;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.mapper.RatingReviewMapper;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.RatingReviewRepository;
import com.fooddelivery.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingReviewServiceTest {
    @Mock private RatingReviewRepository ratingReviewRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Spy private RatingReviewMapper ratingReviewMapper;
    @InjectMocks private RatingReviewService ratingReviewService;

    @Test
    void createsReviewForDeliveredOrderOwnedByCustomer() {
        User customer = customer(1L);
        Order order = order(customer, OrderStatus.DELIVERED);
        CreateRatingReviewRequest request = new CreateRatingReviewRequest(10L, 1L, 5, "Excellent");
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(ratingReviewRepository.existsByOrderIdAndCustomerId(10L, 1L)).thenReturn(false);
        when(ratingReviewRepository.save(any(RatingReview.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RatingReviewResponse response = ratingReviewService.createReview(request);

        assertThat(response.rating()).isEqualTo(5);
        assertThat(response.review()).isEqualTo("Excellent");
    }

    @Test
    void rejectsReviewForUndeliveredOrder() {
        User customer = customer(1L);
        Order order = order(customer, OrderStatus.PREPARING);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> ratingReviewService.createReview(new CreateRatingReviewRequest(10L, 1L, 4, "Soon")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only delivered orders can be rated");
    }

    @Test
    void rejectsReviewFromNonOwner() {
        User owner = customer(2L);
        User requester = customer(1L);
        Order order = order(owner, OrderStatus.DELIVERED);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));

        assertThatThrownBy(() -> ratingReviewService.createReview(new CreateRatingReviewRequest(10L, 1L, 4, "Not mine")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer does not own order: 10");
    }

    private User customer(Long id) {
        User customer = mock(User.class);
        lenient().when(customer.getId()).thenReturn(id);
        lenient().when(customer.getRole()).thenReturn(Role.CUSTOMER);
        return customer;
    }

    private Order order(User customer, OrderStatus status) {
        Order order = mock(Order.class);
        lenient().when(order.getId()).thenReturn(10L);
        lenient().when(order.getCustomer()).thenReturn(customer);
        when(order.getStatus()).thenReturn(status);
        return order;
    }
}
