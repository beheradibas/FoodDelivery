package com.fooddelivery.mapper;

import com.fooddelivery.dto.restaurant.CreateRestaurantRequest;
import com.fooddelivery.dto.restaurant.RestaurantResponse;
import com.fooddelivery.dto.restaurant.UpdateRestaurantRequest;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {
    public Restaurant toEntity(CreateRestaurantRequest request, User owner) {
        return new Restaurant(request.name(), request.city(), request.address(), owner);
    }

    public void updateEntity(Restaurant restaurant, UpdateRestaurantRequest request, User owner) {
        restaurant.update(request.name(), request.city(), request.address(), owner);
    }

    public RestaurantResponse toResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCity(),
                restaurant.getAddress(),
                restaurant.getOwner().getId(),
                restaurant.getCreatedAt());
    }
}
