package com.fooddelivery.service;

import com.fooddelivery.dto.deliveryassignment.DeliveryAssignmentRequest;
import com.fooddelivery.dto.deliveryassignment.DeliveryAssignmentResponse;
import com.fooddelivery.entity.DeliveryAssignment;
import com.fooddelivery.entity.DeliveryPartner;
import com.fooddelivery.entity.Order;
import com.fooddelivery.mapper.DeliveryAssignmentMapper;
import com.fooddelivery.repository.DeliveryAssignmentRepository;
import com.fooddelivery.repository.DeliveryPartnerRepository;
import com.fooddelivery.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryAssignmentServiceTest {
    @Mock private DeliveryAssignmentRepository assignmentRepository;
    @Mock private DeliveryPartnerRepository deliveryPartnerRepository;
    @Mock private OrderRepository orderRepository;
    @Spy private DeliveryAssignmentMapper assignmentMapper;
    @InjectMocks private DeliveryAssignmentService assignmentService;

    @Test
    void assignsOnePartnerToAnOrder() {
        Order order = order(10L);
        DeliveryPartner partner = partner(20L);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(deliveryPartnerRepository.findById(20L)).thenReturn(Optional.of(partner));
        when(assignmentRepository.existsByOrderId(10L)).thenReturn(false);
        when(assignmentRepository.saveAndFlush(any(DeliveryAssignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeliveryAssignmentResponse response = assignmentService.assign(10L, new DeliveryAssignmentRequest(20L));

        assertThat(response.orderId()).isEqualTo(10L);
        assertThat(response.deliveryPartnerId()).isEqualTo(20L);
        verify(orderRepository).saveAndFlush(order);
    }

    @Test
    void rejectsSecondPartnerForSameOrder() {
        Order order = order(10L);
        DeliveryPartner partner = partner(20L);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(deliveryPartnerRepository.findById(20L)).thenReturn(Optional.of(partner));
        when(assignmentRepository.existsByOrderId(10L)).thenReturn(true);

        assertThatThrownBy(() -> assignmentService.assign(10L, new DeliveryAssignmentRequest(20L)))
                .isInstanceOf(com.fooddelivery.exception.DuplicateResourceException.class);
        verify(assignmentRepository, never()).saveAndFlush(any());
    }

    @Test
    void convertsOptimisticConflictToAssignmentConflict() {
        Order order = order(10L);
        DeliveryPartner partner = partner(20L);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(deliveryPartnerRepository.findById(20L)).thenReturn(Optional.of(partner));
        when(assignmentRepository.existsByOrderId(10L)).thenReturn(false);
        when(assignmentRepository.saveAndFlush(any(DeliveryAssignment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.saveAndFlush(order)).thenThrow(new ObjectOptimisticLockingFailureException(Order.class, 10L));

        assertThatThrownBy(() -> assignmentService.assign(10L, new DeliveryAssignmentRequest(20L)))
                .isInstanceOf(com.fooddelivery.exception.DuplicateResourceException.class)
                .hasMessage("Order already has a delivery partner: 10");
    }

    private Order order(Long id) {
        Order order = mock(Order.class);
        lenient().when(order.getId()).thenReturn(id);
        lenient().when(order.getDeliveryAssignment()).thenReturn(null);
        return order;
    }

    private DeliveryPartner partner(Long id) {
        DeliveryPartner partner = mock(DeliveryPartner.class);
        lenient().when(partner.getId()).thenReturn(id);
        return partner;
    }
}
