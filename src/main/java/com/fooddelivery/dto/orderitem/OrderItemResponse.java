package com.fooddelivery.dto.orderitem;

import java.math.BigDecimal;

public record OrderItemResponse(Long id, Long orderId, Long menuItemId, String menuItemName, Integer quantity,
                                BigDecimal unitPrice) { }
