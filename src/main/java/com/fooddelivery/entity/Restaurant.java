package com.fooddelivery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Restaurant() {
    }

    public Restaurant(String name, String city, String address, User owner) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.owner = owner;
    }

    @PrePersist
    void setCreatedAt() {
        createdAt = Instant.now();
    }

    public void update(String name, String city, String address, User owner) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.owner = owner;
    }

}
