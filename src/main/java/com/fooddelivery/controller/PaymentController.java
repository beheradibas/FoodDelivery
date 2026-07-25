package com.fooddelivery.controller;

import com.fooddelivery.dto.payment.CreatePaymentRequest;
import com.fooddelivery.dto.payment.PaymentResponse;
import com.fooddelivery.dto.payment.UpdatePaymentStatusRequest;
import com.fooddelivery.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments() {
        return ResponseEntity.ok(paymentService.getPayments());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(@PathVariable Long paymentId,
                                                                @Valid @RequestBody UpdatePaymentStatusRequest request) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(paymentId, request));
    }
}
