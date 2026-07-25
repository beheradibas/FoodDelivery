package com.fooddelivery.service;

import com.fooddelivery.dto.menuitem.CreateMenuItemRequest;
import com.fooddelivery.dto.menuitem.MenuItemResponse;
import com.fooddelivery.dto.menuitem.UpdateMenuItemRequest;
import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.mapper.MenuItemMapper;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemMapper menuItemMapper;

    public MenuItemService(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository, MenuItemMapper menuItemMapper) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemMapper = menuItemMapper;
    }

    @Transactional
    public MenuItemResponse createMenuItem(Long ownerId, Long restaurantId, CreateMenuItemRequest request) {
        Restaurant restaurant = findOwnedRestaurant(ownerId, restaurantId);
        return menuItemMapper.toResponse(menuItemRepository.save(menuItemMapper.toEntity(request, restaurant)));
    }

    public List<MenuItemResponse> getOwnerMenuItems(Long ownerId, Long restaurantId) {
        findOwnedRestaurant(ownerId, restaurantId);
        return menuItemRepository.findAllByRestaurantId(restaurantId).stream().map(menuItemMapper::toResponse).toList();
    }

    public MenuItemResponse getOwnerMenuItem(Long ownerId, Long restaurantId, Long menuItemId) {
        findOwnedRestaurant(ownerId, restaurantId);
        return menuItemMapper.toResponse(findMenuItem(menuItemId, restaurantId));
    }

    @Transactional
    public MenuItemResponse updateMenuItem(Long ownerId, Long restaurantId, Long menuItemId, UpdateMenuItemRequest request) {
        findOwnedRestaurant(ownerId, restaurantId);
        MenuItem menuItem = findMenuItem(menuItemId, restaurantId);
        menuItemMapper.updateEntity(menuItem, request);
        return menuItemMapper.toResponse(menuItem);
    }

    @Transactional
    public void deleteMenuItem(Long ownerId, Long restaurantId, Long menuItemId) {
        findOwnedRestaurant(ownerId, restaurantId);
        menuItemRepository.delete(findMenuItem(menuItemId, restaurantId));
    }

    public List<MenuItemResponse> getCustomerMenuItems(Long restaurantId) {
        findRestaurant(restaurantId);
        return menuItemRepository.findAllByRestaurantIdAndAvailableTrue(restaurantId).stream()
                .filter(item -> item.getStockQuantity() > 0)
                .map(menuItemMapper::toResponse)
                .toList();
    }

    public MenuItemResponse getCustomerMenuItem(Long restaurantId, Long menuItemId) {
        findRestaurant(restaurantId);
        MenuItem item = menuItemRepository.findByIdAndRestaurantIdAndAvailableTrue(menuItemId, restaurantId)
                .filter(menuItem -> menuItem.getStockQuantity() > 0)
                .orElseThrow(() -> new ResourceNotFoundException("Available menu item not found: " + menuItemId));
        return menuItemMapper.toResponse(item);
    }

    private Restaurant findOwnedRestaurant(Long ownerId, Long restaurantId) {
        Restaurant restaurant = findRestaurant(restaurantId);
        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Restaurant not found: " + restaurantId);
        }
        return restaurant;
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + restaurantId));
    }

    private MenuItem findMenuItem(Long menuItemId, Long restaurantId) {
        return menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + menuItemId));
    }
}
