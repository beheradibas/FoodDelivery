package com.fooddelivery.mapper;

import com.fooddelivery.dto.payment.CreatePaymentRequest;
import com.fooddelivery.dto.payment.PaymentResponse;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public Payment toEntity(CreatePaymentRequest request, User customer) {
        return new Payment(customer, request.paymentReference(), request.amount());
    }

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getCustomer().getId(), payment.getPaymentReference(),
                payment.getAmount(), payment.getStatus(), payment.getCreatedAt());
    }
}
