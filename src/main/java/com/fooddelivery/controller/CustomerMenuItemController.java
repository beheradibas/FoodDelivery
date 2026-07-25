package com.fooddelivery.controller;

import com.fooddelivery.dto.menuitem.MenuItemResponse;
import com.fooddelivery.service.MenuItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menu-items")
public class CustomerMenuItemController {
    private final MenuItemService menuItemService;

    public CustomerMenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getMenuItems(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuItemService.getCustomerMenuItems(restaurantId));
    }

    @GetMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> getMenuItem(@PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        return ResponseEntity.ok(menuItemService.getCustomerMenuItem(restaurantId, menuItemId));
    }
}
