package com.example.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.orderservice.api.dto.CartItemRequest;
import com.example.orderservice.api.dto.CartResponse;
import com.example.orderservice.api.dto.CreateCartItemRequest;
import com.example.orderservice.api.dto.CreateCartRequest;
import com.example.orderservice.api.dto.CreateOrderItemRequest;
import com.example.orderservice.api.dto.CreateOrderRequest;
import com.example.orderservice.api.dto.OrderItemResponse;
import com.example.orderservice.api.dto.OrderItemRequest;
import com.example.orderservice.api.dto.OrderResponse;
import com.example.orderservice.api.dto.UpdateCartItemRequest;
import com.example.orderservice.api.dto.UpdateCartRequest;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.health.OrderServiceHealthIndicator;
import com.example.orderservice.service.CartItemService;
import com.example.orderservice.service.CartService;
import com.example.orderservice.service.OrderItemService;
import com.example.orderservice.service.OrderService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceApplicationTests {
    @Autowired
    private CartService carts;

    @Autowired
    private CartItemService cartItems;

    @Autowired
    private OrderService orders;

    @Autowired
    private OrderItemService orderItems;

    @Autowired
    private OrderServiceHealthIndicator healthIndicator;

    @Autowired
    private WebEndpointsSupplier webEndpointsSupplier;

    @Test
    void contextLoads() {
    }

    @Test
    void actuatorExposesHealthInfoMetricsAndLoggers() {
        Set<String> endpointIds = webEndpointsSupplier.getEndpoints().stream()
                .map(endpoint -> endpoint.getEndpointId().toString())
                .collect(Collectors.toSet());

        assertTrue(endpointIds.containsAll(Set.of("health", "info", "metrics", "loggers")));
        assertEquals("UP", healthIndicator.health().getStatus().getCode());
    }

    @Test
    void cartCrudPersistsAndUpdatesItems() {
        CartResponse created = carts.create(new CreateCartRequest("user-cart-test"));
        carts.addItem(created.id(), new CartItemRequest(12L, 2));

        CartResponse updated = carts.update(
                created.id(),
                new UpdateCartRequest("user-cart-test", List.of(new CartItemRequest(13L, 1))));

        assertEquals(13L, updated.items().getFirst().productId());
        assertEquals(1, updated.items().getFirst().quantity());

        carts.delete(created.id());
        assertThrows(ResponseStatusException.class, () -> carts.findById(created.id()));
    }

    @Test
    void cartItemCrudKeepsParentCartUpdated() {
        CartResponse cart = carts.create(new CreateCartRequest("user-cart-item-test"));
        var created = cartItems.create(new CreateCartItemRequest(cart.id(), 31L, 2));

        var updated = cartItems.update(created.id(), new UpdateCartItemRequest(32L, 5));

        assertEquals(32L, updated.productId());
        assertEquals(5, carts.findById(cart.id()).items().getFirst().quantity());

        cartItems.delete(created.id());
        assertThrows(ResponseStatusException.class, () -> cartItems.findById(created.id()));
    }

    @Test
    void orderCrudPersistsItemsAndCalculatedTotal() {
        OrderResponse created = orders.create(new CreateOrderRequest(
                "ORD-TEST-1",
                "user-order-test",
                "Tunis",
                List.of(new OrderItemRequest(22L, "Test product", 3L, new BigDecimal("2.50"), 3))));

        assertEquals(new BigDecimal("7.50"), created.totalAmount());
        assertEquals(OrderStatus.PENDING, created.status());

        OrderResponse confirmed = orders.changeStatus(created.id(), OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, confirmed.status());
        assertEquals(new BigDecimal("7.50"), orders.findById(created.id()).totalAmount());
    }

    @Test
    void orderItemCrudRecalculatesTotalAndRejectsChangesAfterConfirmation() {
        OrderResponse order = orders.create(new CreateOrderRequest(
                "ORD-ITEM-TEST-" + System.nanoTime(),
                "user-order-item-test",
                "Tunis",
                List.of(new OrderItemRequest(41L, "Original product", 4L, new BigDecimal("2.00"), 2))));
        OrderItemResponse item = orderItems.create(new CreateOrderItemRequest(
                order.id(), 42L, "Additional product", 4L, new BigDecimal("3.00"), 1));

        assertEquals(new BigDecimal("7.00"), orders.findById(order.id()).totalAmount());

        orderItems.update(
                item.id(),
                new OrderItemRequest(42L, "Updated product", 4L, new BigDecimal("4.00"), 2));
        assertEquals(new BigDecimal("12.00"), orders.findById(order.id()).totalAmount());

        orderItems.delete(item.id());
        assertEquals(new BigDecimal("4.00"), orders.findById(order.id()).totalAmount());

        orders.changeStatus(order.id(), OrderStatus.CONFIRMED);
        assertThrows(ResponseStatusException.class, () -> orderItems.delete(order.items().getFirst().id()));
    }
}
