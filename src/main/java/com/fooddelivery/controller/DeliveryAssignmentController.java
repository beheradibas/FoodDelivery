package com.fooddelivery.controller;

import com.fooddelivery.dto.deliveryassignment.DeliveryAssignmentRequest;
import com.fooddelivery.dto.deliveryassignment.DeliveryAssignmentResponse;
import com.fooddelivery.service.DeliveryAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/orders/{orderId}/delivery-assignment")
public class DeliveryAssignmentController {
    private final DeliveryAssignmentService deliveryAssignmentService;

    public DeliveryAssignmentController(DeliveryAssignmentService deliveryAssignmentService) {
        this.deliveryAssignmentService = deliveryAssignmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryAssignmentResponse> assign(@PathVariable Long orderId,
                                                             @Valid @RequestBody DeliveryAssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryAssignmentService.assign(orderId, request));
    }

    @GetMapping
    public ResponseEntity<DeliveryAssignmentResponse> getAssignment(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryAssignmentService.getAssignment(orderId));
    }
}
