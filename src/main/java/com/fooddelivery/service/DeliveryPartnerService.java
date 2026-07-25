package com.fooddelivery.service;

import com.fooddelivery.dto.deliverypartner.CreateDeliveryPartnerRequest;
import com.fooddelivery.dto.deliverypartner.DeliveryPartnerResponse;
import com.fooddelivery.dto.deliverypartner.UpdateDeliveryPartnerRequest;
import com.fooddelivery.entity.DeliveryPartner;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.DuplicateResourceException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.mapper.DeliveryPartnerMapper;
import com.fooddelivery.repository.DeliveryPartnerRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DeliveryPartnerService {
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final UserRepository userRepository;
    private final DeliveryPartnerMapper deliveryPartnerMapper;

    public DeliveryPartnerService(DeliveryPartnerRepository deliveryPartnerRepository, UserRepository userRepository,
                                  DeliveryPartnerMapper deliveryPartnerMapper) {
        this.deliveryPartnerRepository = deliveryPartnerRepository;
        this.userRepository = userRepository;
        this.deliveryPartnerMapper = deliveryPartnerMapper;
    }

    @Transactional
    public DeliveryPartnerResponse createDeliveryPartner(CreateDeliveryPartnerRequest request) {
        if (deliveryPartnerRepository.existsByUserId(request.userId())) {
            throw new DuplicateResourceException("Delivery partner profile already exists for user: " + request.userId());
        }
        User user = findDeliveryPartnerUser(request.userId());
        return deliveryPartnerMapper.toResponse(deliveryPartnerRepository.save(deliveryPartnerMapper.toEntity(request, user)));
    }

    public List<DeliveryPartnerResponse> getDeliveryPartners() {
        return deliveryPartnerRepository.findAll().stream().map(deliveryPartnerMapper::toResponse).toList();
    }

    public DeliveryPartnerResponse getDeliveryPartner(Long id) {
        return deliveryPartnerMapper.toResponse(findDeliveryPartner(id));
    }

    @Transactional
    public DeliveryPartnerResponse updateDeliveryPartner(Long id, UpdateDeliveryPartnerRequest request) {
        DeliveryPartner deliveryPartner = findDeliveryPartner(id);
        deliveryPartnerMapper.updateEntity(deliveryPartner, request);
        return deliveryPartnerMapper.toResponse(deliveryPartner);
    }

    @Transactional
    public void deleteDeliveryPartner(Long id) {
        deliveryPartnerRepository.delete(findDeliveryPartner(id));
    }

    private DeliveryPartner findDeliveryPartner(Long id) {
        return deliveryPartnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found: " + id));
    }

    private User findDeliveryPartnerUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner user not found: " + userId));
        if (user.getRole() != Role.DELIVERY_PARTNER) {
            throw new IllegalArgumentException("User is not a delivery partner: " + userId);
        }
        return user;
    }
}
