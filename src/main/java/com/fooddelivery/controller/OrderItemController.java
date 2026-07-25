package com.fooddelivery.controller;

import com.fooddelivery.dto.orderitem.CreateOrderItemRequest;
import com.fooddelivery.dto.orderitem.OrderItemResponse;
import com.fooddelivery.dto.orderitem.UpdateOrderItemRequest;
import com.fooddelivery.service.OrderItemService;
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

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
public class OrderItemController {
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> addItem(@PathVariable Long orderId,
                                                     @Valid @RequestBody CreateOrderItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemService.addItem(orderId, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getItems(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.getItems(orderId));
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<OrderItemResponse> getItem(@PathVariable Long orderId, @PathVariable Long orderItemId) {
        return ResponseEntity.ok(orderItemService.getItem(orderId, orderItemId));
    }

    @PutMapping("/{orderItemId}")
    public ResponseEntity<OrderItemResponse> updateItem(@PathVariable Long orderId, @PathVariable Long orderItemId,
                                                        @Valid @RequestBody UpdateOrderItemRequest request) {
        return ResponseEntity.ok(orderItemService.updateItem(orderId, orderItemId, request));
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long orderId, @PathVariable Long orderItemId) {
        orderItemService.deleteItem(orderId, orderItemId);
        return ResponseEntity.noContent().build();
    }
}
