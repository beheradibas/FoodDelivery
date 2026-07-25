package com.fooddelivery.controller;

import com.fooddelivery.dto.restaurant.CreateRestaurantRequest;
import com.fooddelivery.dto.restaurant.RestaurantResponse;
import com.fooddelivery.dto.restaurant.UpdateRestaurantRequest;
import com.fooddelivery.service.RestaurantService;
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
@RequestMapping("/api/admin/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.createRestaurant(request));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getRestaurants() {
        return ResponseEntity.ok(restaurantService.getRestaurants());
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurant(restaurantId));
    }

    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable Long restaurantId,
            @Valid @RequestBody UpdateRestaurantRequest request) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId, request));
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }
}
