package com.fooddelivery.service;

import com.fooddelivery.dto.payment.CreatePaymentRequest;
import com.fooddelivery.dto.payment.PaymentResponse;
import com.fooddelivery.dto.payment.UpdatePaymentStatusRequest;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.DuplicateResourceException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.mapper.PaymentMapper;
import com.fooddelivery.repository.PaymentRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        if (paymentRepository.existsByPaymentReference(request.paymentReference())) {
            throw new DuplicateResourceException("Payment reference already exists: " + request.paymentReference());
        }
        User customer = findCustomer(request.customerId());
        return paymentMapper.toResponse(paymentRepository.save(paymentMapper.toEntity(request, customer)));
    }

    public List<PaymentResponse> getPayments() {
        return paymentRepository.findAll().stream().map(paymentMapper::toResponse).toList();
    }

    public PaymentResponse getPayment(Long paymentId) {
        return paymentMapper.toResponse(findPayment(paymentId));
    }

    @Transactional
    public PaymentResponse updatePaymentStatus(Long paymentId, UpdatePaymentStatusRequest request) {
        Payment payment = findPayment(paymentId);
        payment.updateStatus(request.status());
        return paymentMapper.toResponse(payment);
    }

    private Payment findPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
    }

    private User findCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        if (customer.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("User is not a customer: " + customerId);
        }
        return customer;
    }
}
