package com.fooddelivery.dto.payment;

import com.fooddelivery.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStatusRequest(@NotNull(message = "Payment status is required") PaymentStatus status) { }
