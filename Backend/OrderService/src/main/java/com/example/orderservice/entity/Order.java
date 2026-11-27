package com.example.orderservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", uniqueConstraints = @UniqueConstraint(columnNames = "order_number"))
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    private String userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    private String shippingAddress;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
    }

    public Order(String orderNumber, String userId, String shippingAddress) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.shippingAddress = shippingAddress;
    }

    public OrderItem addItem(Long productId, String productName, Long shopId, BigDecimal unitPrice, int quantity) {
        OrderItem item = new OrderItem(this, productId, productName, shopId, unitPrice, quantity);
        items.add(item);
        calculateTotal();
        return item;
    }

    public boolean updateItem(
            Long itemId, Long productId, String productName, Long shopId, BigDecimal unitPrice, int quantity) {
        OrderItem item = items.stream()
                .filter(existing -> existing.getId().equals(itemId))
                .findFirst()
                .orElse(null);
        if (item == null) {
            return false;
        }
        item.update(productId, productName, shopId, unitPrice, quantity);
        calculateTotal();
        return true;
    }

    public boolean removeItem(Long itemId) {
        boolean removed = items.removeIf(item -> item.getId().equals(itemId));
        if (removed) {
            calculateTotal();
        }
        return removed;
    }

    public void clearItems() {
        items.clear();
        calculateTotal();
    }

    public BigDecimal calculateTotal() {
        totalAmount = items.stream()
                .map(OrderItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalAmount;
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING;
    }

    public void changeStatus(OrderStatus nextStatus) {
        boolean allowed = switch (status) {
            case PENDING -> nextStatus == OrderStatus.CONFIRMED || nextStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> nextStatus == OrderStatus.SHIPPED;
            case SHIPPED -> nextStatus == OrderStatus.DELIVERED;
            case CANCELLED, DELIVERED -> false;
        };
        if (!allowed) {
            throw new IllegalStateException("Transition de statut invalide: " + status + " -> " + nextStatus);
        }
        status = nextStatus;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @PrePersist
    private void initializeCreatedAt() {
        createdAt = LocalDateTime.now();
        calculateTotal();
    }

    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
