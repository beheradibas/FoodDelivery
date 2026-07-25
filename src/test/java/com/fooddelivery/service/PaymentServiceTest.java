package com.fooddelivery.service;

import com.fooddelivery.dto.payment.CreatePaymentRequest;
import com.fooddelivery.dto.payment.PaymentResponse;
import com.fooddelivery.dto.payment.UpdatePaymentStatusRequest;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.PaymentStatus;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.DuplicateResourceException;
import com.fooddelivery.mapper.PaymentMapper;
import com.fooddelivery.repository.PaymentRepository;
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
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPaymentCreatesPendingPaymentForCustomer() {
        User customer = customer(1L);
        CreatePaymentRequest request = new CreatePaymentRequest(1L, "PAY-1001", new BigDecimal("499.00"));
        when(paymentRepository.existsByPaymentReference("PAY-1001")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.createPayment(request);

        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.amount()).isEqualByComparingTo("499.00");
    }

    @Test
    void createPaymentRejectsDuplicatePaymentReference() {
        CreatePaymentRequest request = new CreatePaymentRequest(1L, "PAY-1001", new BigDecimal("499.00"));
        when(paymentRepository.existsByPaymentReference("PAY-1001")).thenReturn(true);

        assertThatThrownBy(() -> paymentService.createPayment(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Payment reference already exists: PAY-1001");
    }

    @Test
    void updatePaymentStatusUpdatesStoredPayment() {
        User customer = customer(1L);
        Payment payment = new Payment(customer, "PAY-1001", new BigDecimal("499.00"));
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.updatePaymentStatus(1L, new UpdatePaymentStatusRequest(PaymentStatus.SUCCESS));

        assertThat(response.status()).isEqualTo(PaymentStatus.SUCCESS);
    }

    private User customer(Long id) {
        User customer = mock(User.class);
        when(customer.getId()).thenReturn(id);
        lenient().when(customer.getRole()).thenReturn(Role.CUSTOMER);
        return customer;
    }
}
