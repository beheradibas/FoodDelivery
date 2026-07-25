package com.fooddelivery.service;

import com.fooddelivery.dto.restaurant.CreateRestaurantRequest;
import com.fooddelivery.dto.restaurant.RestaurantResponse;
import com.fooddelivery.dto.restaurant.UpdateRestaurantRequest;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.mapper.RestaurantMapper;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantService(RestaurantRepository restaurantRepository, UserRepository userRepository, RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.restaurantMapper = restaurantMapper;
    }

    @Transactional
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        User owner = findRestaurantOwner(request.ownerId());
        Restaurant restaurant = restaurantMapper.toEntity(request, owner);
        return restaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    public List<RestaurantResponse> getRestaurants() {
        return restaurantRepository.findAll().stream().map(restaurantMapper::toResponse).toList();
    }

    public RestaurantResponse getRestaurant(Long restaurantId) {
        return restaurantMapper.toResponse(findRestaurant(restaurantId));
    }

    @Transactional
    public RestaurantResponse updateRestaurant(Long restaurantId, UpdateRestaurantRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        User owner = findRestaurantOwner(request.ownerId());
        restaurantMapper.updateEntity(restaurant, request, owner);
        return restaurantMapper.toResponse(restaurant);
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        restaurantRepository.delete(findRestaurant(restaurantId));
    }

    public List<RestaurantResponse> getOwnerRestaurants(Long ownerId) {
        findRestaurantOwner(ownerId);
        return restaurantRepository.findAllByOwnerId(ownerId).stream().map(restaurantMapper::toResponse).toList();
    }

    public RestaurantResponse getOwnerRestaurant(Long ownerId, Long restaurantId) {
        Restaurant restaurant = findRestaurant(restaurantId);
        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Restaurant not found: " + restaurantId);
        }
        return restaurantMapper.toResponse(restaurant);
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + restaurantId));
    }

    private User findRestaurantOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant owner not found: " + ownerId));
        if (owner.getRole() != Role.RESTAURANT_OWNER) {
            throw new IllegalArgumentException("User is not a restaurant owner: " + ownerId);
        }
        return owner;
    }
}
