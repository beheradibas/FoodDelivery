package com.fooddelivery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "rating_reviews", uniqueConstraints = @UniqueConstraint(name = "uk_rating_review_order_customer", columnNames = {"order_id", "customer_id"}))
public class RatingReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String review;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected RatingReview() {
    }

    public RatingReview(Order order, User customer, Integer rating, String review) {
        this.order = order;
        this.customer = customer;
        this.rating = rating;
        this.review = review;
    }

    @PrePersist
    void setCreatedAt() {
        createdAt = Instant.now();
    }
}
