package com.fooddelivery.service;

import com.fooddelivery.dto.restaurant.CreateRestaurantRequest;
import com.fooddelivery.dto.restaurant.RestaurantResponse;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.mapper.RestaurantMapper;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private RestaurantService restaurantService;

    private User restaurantOwner;

    @BeforeEach
    void setUp() {
        restaurantOwner = new User("Ravi", "Kumar", "ravi@example.com", "encoded-password", Role.RESTAURANT_OWNER);
    }

    @Test
    void createRestaurantAssignsRestaurantOwnerAndCity() {
        CreateRestaurantRequest request = new CreateRestaurantRequest("Spice House", "Pune", "12 Market Road", 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(restaurantOwner));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RestaurantResponse response = restaurantService.createRestaurant(request);

        verify(restaurantRepository).save(any(Restaurant.class));
        assertThat(response.name()).isEqualTo("Spice House");
        assertThat(response.city()).isEqualTo("Pune");
        assertThat(response.ownerId()).isEqualTo(restaurantOwner.getId());
    }

    @Test
    void createRestaurantRejectsUserWhoIsNotRestaurantOwner() {
        User customer = new User("Asha", "Sharma", "asha@example.com", "encoded-password", Role.CUSTOMER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        CreateRestaurantRequest request = new CreateRestaurantRequest("Spice House", "Pune", "12 Market Road", 1L);

        assertThatThrownBy(() -> restaurantService.createRestaurant(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User is not a restaurant owner: 1");
    }

    @Test
    void getOwnerRestaurantsReturnsOnlyRestaurantsForThatOwner() {
        Restaurant restaurant = new Restaurant("Spice House", "Pune", "12 Market Road", restaurantOwner);
        when(userRepository.findById(1L)).thenReturn(Optional.of(restaurantOwner));
        when(restaurantRepository.findAllByOwnerId(1L)).thenReturn(List.of(restaurant));

        List<RestaurantResponse> response = restaurantService.getOwnerRestaurants(1L);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().city()).isEqualTo("Pune");
        verify(restaurantRepository).findAllByOwnerId(1L);
    }
}
