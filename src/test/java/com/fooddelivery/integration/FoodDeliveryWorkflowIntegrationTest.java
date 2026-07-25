package com.fooddelivery.integration;

import com.fooddelivery.dto.deliveryassignment.DeliveryAssignmentRequest;
import com.fooddelivery.dto.deliveryassignment.DeliveryAssignmentResponse;
import com.fooddelivery.dto.deliverypartner.CreateDeliveryPartnerRequest;
import com.fooddelivery.dto.menuitem.CreateMenuItemRequest;
import com.fooddelivery.dto.menuitem.MenuItemResponse;
import com.fooddelivery.dto.menuitem.UpdateMenuItemRequest;
import com.fooddelivery.dto.order.OrderPlacementResponse;
import com.fooddelivery.dto.order.PlaceOrderRequest;
import com.fooddelivery.dto.order.UpdateOrderStatusRequest;
import com.fooddelivery.dto.orderitem.PlaceOrderItemRequest;
import com.fooddelivery.dto.restaurant.CreateRestaurantRequest;
import com.fooddelivery.dto.restaurant.RestaurantResponse;
import com.fooddelivery.dto.review.CreateRatingReviewRequest;
import com.fooddelivery.dto.review.RatingReviewResponse;
import com.fooddelivery.entity.DeliveryPartner;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.OrderStatus;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.Role;
import com.fooddelivery.entity.User;
import com.fooddelivery.repository.DeliveryPartnerRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.PaymentRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.DeliveryAssignmentService;
import com.fooddelivery.service.DeliveryPartnerService;
import com.fooddelivery.service.MenuItemService;
import com.fooddelivery.service.OrderPlacementService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.RatingReviewService;
import com.fooddelivery.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class FoodDeliveryWorkflowIntegrationTest extends AbstractIntegrationTest {
    @Autowired private UserRepository userRepository;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private RestaurantService restaurantService;
    @Autowired private MenuItemService menuItemService;
    @Autowired private OrderPlacementService orderPlacementService;
    @Autowired private DeliveryPartnerService deliveryPartnerService;
    @Autowired private DeliveryAssignmentService deliveryAssignmentService;
    @Autowired private OrderService orderService;
    @Autowired private RatingReviewService ratingReviewService;

    @Test
    void createsRestaurantForOwner() {
        User owner = saveUser("restaurant-owner@test.com", Role.RESTAURANT_OWNER);

        RestaurantResponse response = restaurantService.createRestaurant(
                new CreateRestaurantRequest("Integration Kitchen", "Pune", "1 Main Road", owner.getId()));

        assertThat(response.name()).isEqualTo("Integration Kitchen");
        assertThat(response.city()).isEqualTo("Pune");
        assertThat(response.ownerId()).isEqualTo(owner.getId());
    }

    @Test
    void performsMenuCrudForRestaurantOwner() {
        User owner = saveUser("menu-owner@test.com", Role.RESTAURANT_OWNER);
        Restaurant restaurant = saveRestaurant(owner, "Menu Kitchen");

        MenuItemResponse created = menuItemService.createMenuItem(owner.getId(), restaurant.getId(),
                new CreateMenuItemRequest("Thali", "Meal", new BigDecimal("150.00"), 10, true));
        assertThat(created.name()).isEqualTo("Thali");

        MenuItemResponse updated = menuItemService.updateMenuItem(owner.getId(), restaurant.getId(), created.id(),
                new UpdateMenuItemRequest("Special Thali", "Large meal", new BigDecimal("180.00"), 8, true));
        assertThat(updated.name()).isEqualTo("Special Thali");
        assertThat(menuItemService.getOwnerMenuItems(owner.getId(), restaurant.getId())).hasSize(1);

        menuItemService.deleteMenuItem(owner.getId(), restaurant.getId(), created.id());
        assertThat(menuItemService.getOwnerMenuItems(owner.getId(), restaurant.getId())).isEmpty();
    }

    @Test
    void placesOrderAndCreatesPaymentAndOrderItems() {
        User customer = saveUser("order-customer@test.com", Role.CUSTOMER);
        User owner = saveUser("order-owner@test.com", Role.RESTAURANT_OWNER);
        Restaurant restaurant = saveRestaurant(owner, "Order Kitchen");
        MenuItemService menu = menuItemService;
        MenuItemResponse item = menu.createMenuItem(owner.getId(), restaurant.getId(),
                new CreateMenuItemRequest("Biryani", "Rice meal", new BigDecimal("220.00"), 5, true));

        OrderPlacementResponse response = orderPlacementService.placeOrder(new PlaceOrderRequest(
                customer.getId(), restaurant.getId(), "INTEGRATION-WORKFLOW-PAYMENT",
                List.of(new PlaceOrderItemRequest(item.id(), 2))));

        assertThat(response.order().status()).isEqualTo(OrderStatus.PLACED);
        assertThat(response.items()).hasSize(1);
        assertThat(paymentRepository.count()).isEqualTo(1);
        assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void assignsOneDeliveryPartnerToOrder() {
        User customer = saveUser("assignment-customer@test.com", Role.CUSTOMER);
        User owner = saveUser("assignment-owner@test.com", Role.RESTAURANT_OWNER);
        User partnerUser = saveUser("assignment-partner@test.com", Role.DELIVERY_PARTNER);
        Restaurant restaurant = saveRestaurant(owner, "Assignment Kitchen");
        Payment payment = paymentRepository.save(new Payment(customer, "ASSIGNMENT-PAYMENT", new BigDecimal("100.00")));
        Order order = orderRepository.save(new Order(customer, restaurant, payment));
        deliveryPartnerService.createDeliveryPartner(new CreateDeliveryPartnerRequest(partnerUser.getId(), "Pune"));
        DeliveryPartner partner = deliveryPartnerRepository.findAll().getFirst();

        DeliveryAssignmentResponse response = deliveryAssignmentService.assign(order.getId(),
                new DeliveryAssignmentRequest(partner.getId()));

        assertThat(response.orderId()).isEqualTo(order.getId());
        assertThat(response.deliveryPartnerId()).isEqualTo(partner.getId());
    }

    @Test
    void updatesOrderThroughValidLifecycle() {
        Order order = saveOrder("status-customer@test.com", "status-owner@test.com", "STATUS-PAYMENT");

        orderService.updateOrderStatus(order.getId(), new UpdateOrderStatusRequest(OrderStatus.ACCEPTED));
        orderService.updateOrderStatus(order.getId(), new UpdateOrderStatusRequest(OrderStatus.PREPARING));
        orderService.updateOrderStatus(order.getId(), new UpdateOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY));
        var response = orderService.updateOrderStatus(order.getId(), new UpdateOrderStatusRequest(OrderStatus.DELIVERED));

        assertThat(response.status()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void createsReviewOnlyAfterDelivery() {
        User customer = saveUser("review-customer@test.com", Role.CUSTOMER);
        Order order = saveOrder(customer, "review-owner@test.com", "REVIEW-PAYMENT");
        order.updateStatus(OrderStatus.DELIVERED);

        RatingReviewResponse response = ratingReviewService.createReview(
                new CreateRatingReviewRequest(order.getId(), customer.getId(), 5, "Excellent food"));

        assertThat(response.orderId()).isEqualTo(order.getId());
        assertThat(response.rating()).isEqualTo(5);
    }

    @Autowired private DeliveryPartnerRepository deliveryPartnerRepository;

    private User saveUser(String email, Role role) {
        return userRepository.save(new User("Test", "User", email, "encoded-password", role));
    }

    private Restaurant saveRestaurant(User owner, String name) {
        return restaurantRepository.save(new Restaurant(name, "Pune", "Test Address", owner));
    }

    private Order saveOrder(String customerEmail, String ownerEmail, String paymentReference) {
        return saveOrder(saveUser(customerEmail, Role.CUSTOMER), ownerEmail, paymentReference);
    }

    private Order saveOrder(User customer, String ownerEmail, String paymentReference) {
        User owner = saveUser(ownerEmail, Role.RESTAURANT_OWNER);
        Restaurant restaurant = saveRestaurant(owner, "Status Kitchen");
        Payment payment = paymentRepository.save(new Payment(customer, paymentReference, new BigDecimal("100.00")));
        return orderRepository.save(new Order(customer, restaurant, payment));
    }
}
