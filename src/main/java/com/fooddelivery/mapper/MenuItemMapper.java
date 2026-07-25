package com.fooddelivery.mapper;

import com.fooddelivery.dto.menuitem.CreateMenuItemRequest;
import com.fooddelivery.dto.menuitem.MenuItemResponse;
import com.fooddelivery.dto.menuitem.UpdateMenuItemRequest;
import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {
    public MenuItem toEntity(CreateMenuItemRequest request, Restaurant restaurant) {
        return new MenuItem(restaurant, request.name(), request.description(), request.price(), request.stockQuantity(), request.available());
    }

    public void updateEntity(MenuItem menuItem, UpdateMenuItemRequest request) {
        menuItem.update(request.name(), request.description(), request.price(), request.stockQuantity(), request.available());
    }

    public MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(item.getId(), item.getRestaurant().getId(), item.getName(), item.getDescription(),
                item.getPrice(), item.getStockQuantity(), item.isAvailable());
    }
}
