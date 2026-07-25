package com.fooddelivery.controller;

import com.fooddelivery.dto.restaurant.RestaurantResponse;
import com.fooddelivery.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/owners/{ownerId}/restaurants")
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class OwnerRestaurantController {
    private final RestaurantService restaurantService;

    public OwnerRestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getOwnerRestaurants(@PathVariable Long ownerId) {
        return ResponseEntity.ok(restaurantService.getOwnerRestaurants(ownerId));
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getOwnerRestaurant(
            @PathVariable Long ownerId,
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getOwnerRestaurant(ownerId, restaurantId));
    }
}
