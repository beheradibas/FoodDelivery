package com.fooddelivery.mapper;

import com.fooddelivery.dto.deliverypartner.CreateDeliveryPartnerRequest;
import com.fooddelivery.dto.deliverypartner.DeliveryPartnerResponse;
import com.fooddelivery.dto.deliverypartner.UpdateDeliveryPartnerRequest;
import com.fooddelivery.entity.DeliveryPartner;
import com.fooddelivery.entity.User;
import org.springframework.stereotype.Component;

@Component
public class DeliveryPartnerMapper {
    public DeliveryPartner toEntity(CreateDeliveryPartnerRequest request, User user) {
        return new DeliveryPartner(user, request.city());
    }

    public void updateEntity(DeliveryPartner deliveryPartner, UpdateDeliveryPartnerRequest request) {
        deliveryPartner.update(request.city());
    }

    public DeliveryPartnerResponse toResponse(DeliveryPartner deliveryPartner) {
        return new DeliveryPartnerResponse(deliveryPartner.getId(), deliveryPartner.getUser().getId(), deliveryPartner.getCity());
    }
}
