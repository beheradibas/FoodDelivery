package com.fooddelivery.controller;

import com.fooddelivery.dto.deliverypartner.CreateDeliveryPartnerRequest;
import com.fooddelivery.dto.deliverypartner.DeliveryPartnerResponse;
import com.fooddelivery.dto.deliverypartner.UpdateDeliveryPartnerRequest;
import com.fooddelivery.service.DeliveryPartnerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/delivery-partners")
public class DeliveryPartnerController {
    private final DeliveryPartnerService deliveryPartnerService;

    public DeliveryPartnerController(DeliveryPartnerService deliveryPartnerService) {
        this.deliveryPartnerService = deliveryPartnerService;
    }

    @PostMapping
    public ResponseEntity<DeliveryPartnerResponse> createDeliveryPartner(@Valid @RequestBody CreateDeliveryPartnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryPartnerService.createDeliveryPartner(request));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryPartnerResponse>> getDeliveryPartners() {
        return ResponseEntity.ok(deliveryPartnerService.getDeliveryPartners());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryPartnerResponse> getDeliveryPartner(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryPartnerService.getDeliveryPartner(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryPartnerResponse> updateDeliveryPartner(@PathVariable Long id,
                                                                          @Valid @RequestBody UpdateDeliveryPartnerRequest request) {
        return ResponseEntity.ok(deliveryPartnerService.updateDeliveryPartner(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryPartner(@PathVariable Long id) {
        deliveryPartnerService.deleteDeliveryPartner(id);
        return ResponseEntity.noContent().build();
    }
}
