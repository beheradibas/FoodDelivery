package com.fooddelivery.service;

import com.fooddelivery.dto.menuitem.CreateMenuItemRequest;
import com.fooddelivery.dto.menuitem.MenuItemResponse;
import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.User;
import com.fooddelivery.mapper.MenuItemMapper;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {
    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Spy
    private MenuItemMapper menuItemMapper;

    @InjectMocks
    private MenuItemService menuItemService;

    @Test
    void createMenuItemAddsItemToOwnedRestaurant() {
        Restaurant restaurant = ownedRestaurant(1L);
        when(restaurant.getId()).thenReturn(10L);
        CreateMenuItemRequest request = new CreateMenuItemRequest(
                "Paneer Tikka", "Char-grilled paneer", new BigDecimal("299.00"), 20, true);
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MenuItemResponse response = menuItemService.createMenuItem(1L, 10L, request);

        verify(menuItemRepository).save(any(MenuItem.class));
        assertThat(response.restaurantId()).isEqualTo(10L);
        assertThat(response.price()).isEqualByComparingTo("299.00");
        assertThat(response.stockQuantity()).isEqualTo(20);
    }

    @Test
    void customerViewExcludesItemsWithNoStock() {
        Restaurant restaurant = ownedRestaurant(1L);
        MenuItem outOfStockItem = new MenuItem(restaurant, "Paneer Tikka", "Char-grilled paneer", new BigDecimal("299.00"), 0, true);
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findAllByRestaurantIdAndAvailableTrue(10L)).thenReturn(List.of(outOfStockItem));

        List<MenuItemResponse> response = menuItemService.getCustomerMenuItems(10L);

        assertThat(response).isEmpty();
    }

    @Test
    void ownerCannotManageAnotherOwnersRestaurant() {
        Restaurant restaurant = ownedRestaurant(2L);
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        CreateMenuItemRequest request = new CreateMenuItemRequest(
                "Paneer Tikka", "Char-grilled paneer", new BigDecimal("299.00"), 20, true);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> menuItemService.createMenuItem(1L, 10L, request))
                .isInstanceOf(com.fooddelivery.exception.ResourceNotFoundException.class)
                .hasMessage("Restaurant not found: 10");
    }

    private Restaurant ownedRestaurant(Long ownerId) {
        Restaurant restaurant = mock(Restaurant.class);
        User owner = mock(User.class);
        lenient().when(owner.getId()).thenReturn(ownerId);
        lenient().when(restaurant.getOwner()).thenReturn(owner);
        return restaurant;
    }
}
