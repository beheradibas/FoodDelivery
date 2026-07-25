package com.fooddelivery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "delivery_assignments", uniqueConstraints = @UniqueConstraint(name = "uk_delivery_assignment_order", columnNames = "order_id"))
public class DeliveryAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_partner_id", nullable = false)
    private DeliveryPartner deliveryPartner;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private Instant assignedAt;

    protected DeliveryAssignment() {
    }

    public DeliveryAssignment(Order order, DeliveryPartner deliveryPartner) {
        this.order = order;
        this.deliveryPartner = deliveryPartner;
        this.assignedAt = Instant.now();
    }
}
