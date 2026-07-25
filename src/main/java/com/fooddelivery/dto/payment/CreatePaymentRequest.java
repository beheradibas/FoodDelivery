package com.fooddelivery.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull(message = "Customer ID is required") Long customerId,
        @NotBlank(message = "Payment reference is required") @Size(max = 100, message = "Payment reference must not exceed 100 characters") String paymentReference,
        @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount
) { }
