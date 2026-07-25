package com.fooddelivery.dto.restaurant;

import java.time.Instant;

public record RestaurantResponse(Long id, String name, String city, String address, Long ownerId, Instant createdAt) {
}
