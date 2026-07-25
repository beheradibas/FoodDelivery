package com.fooddelivery.mapper;

import com.fooddelivery.dto.deliveryassignment.DeliveryAssignmentResponse;
import com.fooddelivery.entity.DeliveryAssignment;
import org.springframework.stereotype.Component;

@Component
public class DeliveryAssignmentMapper {
    public DeliveryAssignmentResponse toResponse(DeliveryAssignment assignment) {
        return new DeliveryAssignmentResponse(assignment.getId(), assignment.getOrder().getId(),
                assignment.getDeliveryPartner().getId(), assignment.getAssignedAt());
    }
}
