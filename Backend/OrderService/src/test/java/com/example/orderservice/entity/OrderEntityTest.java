package com.example.orderservice.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderEntityTest {
    @Test
    void calculatesTotalAndAllowsOnlyValidStatusTransitions() {
        Order order = new Order("ORD-1", "user-1", "Tunis");
        order.addItem(1L, "Product A", 10L, new BigDecimal("1.25"), 2);
        order.addItem(2L, "Product B", 11L, new BigDecimal("5.00"), 1);

        assertEquals(new BigDecimal("7.50"), order.calculateTotal());
        assertTrue(order.canBeCancelled());

        order.changeStatus(OrderStatus.CONFIRMED);
        assertFalse(order.canBeCancelled());
        assertThrows(IllegalStateException.class, () -> order.changeStatus(OrderStatus.CANCELLED));

        order.changeStatus(OrderStatus.SHIPPED);
        order.changeStatus(OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void cartMergesQuantitiesForTheSameProduct() {
        Cart cart = new Cart("user-1");
        cart.addItem(42L, 2);
        cart.addItem(42L, 3);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().getFirst().getQuantity());
    }
}
