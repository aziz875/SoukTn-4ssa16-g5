package com.example.orderservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    protected Cart() {
    }

    public Cart(String userId) {
        this.userId = userId;
    }

    public CartItem addItem(Long productId, int quantity) {
        CartItem item = items.stream()
                .filter(existing -> existing.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
        if (item == null) {
            item = new CartItem(this, productId, quantity);
            items.add(item);
        } else {
            item.increaseQuantity(quantity);
        }
        touch();
        return item;
    }

    public CartItem updateItem(Long itemId, Long productId, int quantity) {
        CartItem item = items.stream()
                .filter(existing -> existing.getId().equals(itemId))
                .findFirst()
                .orElse(null);
        if (item == null) {
            return null;
        }
        boolean duplicateProduct = items.stream()
                .anyMatch(existing -> existing != item && existing.getProductId().equals(productId));
        if (duplicateProduct) {
            throw new IllegalArgumentException("Le produit existe déjà dans ce panier");
        }
        item.update(productId, quantity);
        touch();
        return item;
    }

    public boolean removeItem(Long productId) {
        boolean removed = items.removeIf(item -> item.getProductId().equals(productId));
        if (removed) {
            touch();
        }
        return removed;
    }

    public boolean removeItemById(Long itemId) {
        boolean removed = items.removeIf(item -> item.getId().equals(itemId));
        if (removed) {
            touch();
        }
        return removed;
    }

    public void clear() {
        items.clear();
        touch();
    }

    public void setUserId(String userId) {
        this.userId = userId;
        touch();
    }

    @PrePersist
    @PreUpdate
    private void updateTimestamp() {
        updatedAt = LocalDateTime.now();
    }

    private void touch() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<CartItem> getItems() {
        return items;
    }
}
