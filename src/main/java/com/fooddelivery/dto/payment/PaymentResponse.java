package com.fooddelivery.dto.payment;

import com.fooddelivery.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(Long id, Long customerId, String paymentReference, BigDecimal amount, PaymentStatus status,
                              Instant createdAt) { }
