package com.fooddelivery.service;

import com.fooddelivery.dto.deliveryassignment.DeliveryAssignmentRequest;
import com.fooddelivery.dto.deliveryassignment.DeliveryAssignmentResponse;
import com.fooddelivery.entity.DeliveryAssignment;
import com.fooddelivery.entity.DeliveryPartner;
import com.fooddelivery.entity.Order;
import com.fooddelivery.exception.DuplicateResourceException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.mapper.DeliveryAssignmentMapper;
import com.fooddelivery.repository.DeliveryAssignmentRepository;
import com.fooddelivery.repository.DeliveryPartnerRepository;
import com.fooddelivery.repository.OrderRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryAssignmentService {
    private final DeliveryAssignmentRepository assignmentRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final OrderRepository orderRepository;
    private final DeliveryAssignmentMapper assignmentMapper;

    public DeliveryAssignmentService(DeliveryAssignmentRepository assignmentRepository,
                                     DeliveryPartnerRepository deliveryPartnerRepository,
                                     OrderRepository orderRepository,
                                     DeliveryAssignmentMapper assignmentMapper) {
        this.assignmentRepository = assignmentRepository;
        this.deliveryPartnerRepository = deliveryPartnerRepository;
        this.orderRepository = orderRepository;
        this.assignmentMapper = assignmentMapper;
    }

    @Transactional
    public DeliveryAssignmentResponse assign(Long orderId, DeliveryAssignmentRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(request.deliveryPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found: " + request.deliveryPartnerId()));
        if (order.getDeliveryAssignment() != null || assignmentRepository.existsByOrderId(orderId)) {
            throw new DuplicateResourceException("Order already has a delivery partner: " + orderId);
        }

        DeliveryAssignment assignment = new DeliveryAssignment(order, deliveryPartner);
        order.assignDeliveryPartner(assignment);
        try {
            assignmentRepository.saveAndFlush(assignment);
            orderRepository.saveAndFlush(order);
            return assignmentMapper.toResponse(assignment);
        } catch (ObjectOptimisticLockingFailureException | DataIntegrityViolationException exception) {
            throw new DuplicateResourceException("Order already has a delivery partner: " + orderId);
        }
    }

    @Transactional(readOnly = true)
    public DeliveryAssignmentResponse getAssignment(Long orderId) {
        return assignmentMapper.toResponse(assignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery assignment not found for order: " + orderId)));
    }
}
