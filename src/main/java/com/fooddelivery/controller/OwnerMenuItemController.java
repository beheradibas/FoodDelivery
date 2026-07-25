package com.fooddelivery.controller;

import com.fooddelivery.dto.menuitem.CreateMenuItemRequest;
import com.fooddelivery.dto.menuitem.MenuItemResponse;
import com.fooddelivery.dto.menuitem.UpdateMenuItemRequest;
import com.fooddelivery.service.MenuItemService;
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
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/owners/{ownerId}/restaurants/{restaurantId}/menu-items")
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class OwnerMenuItemController {
    private final MenuItemService menuItemService;

    public OwnerMenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> createMenuItem(@PathVariable Long ownerId, @PathVariable Long restaurantId,
                                                            @Valid @RequestBody CreateMenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.createMenuItem(ownerId, restaurantId, request));
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getMenuItems(@PathVariable Long ownerId, @PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuItemService.getOwnerMenuItems(ownerId, restaurantId));
    }

    @GetMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> getMenuItem(@PathVariable Long ownerId, @PathVariable Long restaurantId,
                                                         @PathVariable Long menuItemId) {
        return ResponseEntity.ok(menuItemService.getOwnerMenuItem(ownerId, restaurantId, menuItemId));
    }

    @PutMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@PathVariable Long ownerId, @PathVariable Long restaurantId,
                                                            @PathVariable Long menuItemId, @Valid @RequestBody UpdateMenuItemRequest request) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(ownerId, restaurantId, menuItemId, request));
    }

    @DeleteMapping("/{menuItemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long ownerId, @PathVariable Long restaurantId,
                                               @PathVariable Long menuItemId) {
        menuItemService.deleteMenuItem(ownerId, restaurantId, menuItemId);
        return ResponseEntity.noContent().build();
    }
}
