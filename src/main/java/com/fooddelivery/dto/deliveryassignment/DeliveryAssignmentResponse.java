package com.fooddelivery.dto.deliveryassignment;

import java.time.Instant;

public record DeliveryAssignmentResponse(Long id, Long orderId, Long deliveryPartnerId, Instant assignedAt) { }
