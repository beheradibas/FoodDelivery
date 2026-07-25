package com.fooddelivery.service;

import com.fooddelivery.dto.orderitem.CreateOrderItemRequest;
import com.fooddelivery.dto.orderitem.OrderItemResponse;
import com.fooddelivery.dto.orderitem.UpdateOrderItemRequest;
import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.OrderItem;
import com.fooddelivery.exception.DuplicateResourceException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.mapper.OrderItemMapper;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.OrderItemRepository;
import com.fooddelivery.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemMapper orderItemMapper;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository,
                            MenuItemRepository menuItemRepository, OrderItemMapper orderItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderItemMapper = orderItemMapper;
    }

    @Transactional
    public OrderItemResponse addItem(Long orderId, CreateOrderItemRequest request) {
        Order order = findOrder(orderId);
        MenuItem menuItem = findMenuItem(request.menuItemId());
        ensureMenuItemBelongsToOrderRestaurant(order, menuItem);
        if (orderItemRepository.existsByOrderIdAndMenuItemId(orderId, request.menuItemId())) {
            throw new DuplicateResourceException("Menu item is already included in order: " + orderId);
        }
        return orderItemMapper.toResponse(orderItemRepository.save(new OrderItem(order, menuItem, request.quantity())));
    }

    public List<OrderItemResponse> getItems(Long orderId) {
        findOrder(orderId);
        return orderItemRepository.findAllByOrderId(orderId).stream().map(orderItemMapper::toResponse).toList();
    }

    public OrderItemResponse getItem(Long orderId, Long orderItemId) {
        findOrder(orderId);
        return orderItemMapper.toResponse(findOrderItem(orderId, orderItemId));
    }

    @Transactional
    public OrderItemResponse updateItem(Long orderId, Long orderItemId, UpdateOrderItemRequest request) {
        OrderItem orderItem = findOrderItem(orderId, orderItemId);
        orderItem.updateQuantity(request.quantity());
        return orderItemMapper.toResponse(orderItem);
    }

    @Transactional
    public void deleteItem(Long orderId, Long orderItemId) {
        orderItemRepository.delete(findOrderItem(orderId, orderItemId));
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    private MenuItem findMenuItem(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + menuItemId));
    }

    private OrderItem findOrderItem(Long orderId, Long orderItemId) {
        return orderItemRepository.findByIdAndOrderId(orderItemId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found: " + orderItemId));
    }

    private void ensureMenuItemBelongsToOrderRestaurant(Order order, MenuItem menuItem) {
        if (!order.getRestaurant().getId().equals(menuItem.getRestaurant().getId())) {
            throw new IllegalArgumentException("Menu item does not belong to order restaurant");
        }
    }
}
