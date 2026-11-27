package com.example.orderservice.controller;

import com.example.orderservice.api.dto.CartItemRequest;
import com.example.orderservice.api.dto.CartResponse;
import com.example.orderservice.api.dto.CreateCartRequest;
import com.example.orderservice.api.dto.UpdateCartRequest;
import com.example.orderservice.service.CartService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService carts;

    public CartController(CartService carts) {
        this.carts = carts;
    }

    @PostMapping
    public ResponseEntity<CartResponse> create(@Valid @RequestBody CreateCartRequest request) {
        CartResponse cart = carts.create(request);
        return ResponseEntity.created(URI.create("/api/carts/" + cart.id())).body(cart);
    }

    @GetMapping
    public List<CartResponse> findAll() {
        return carts.findAll();
    }

    @GetMapping("/{id}")
    public CartResponse findById(@PathVariable Long id) {
        return carts.findById(id);
    }

    @PutMapping("/{id}")
    public CartResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCartRequest request) {
        return carts.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carts.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/items")
    public CartResponse addItem(@PathVariable Long id, @Valid @RequestBody CartItemRequest request) {
        return carts.addItem(id, request);
    }

    @DeleteMapping("/{id}/items/{productId}")
    public CartResponse removeItem(@PathVariable Long id, @PathVariable Long productId) {
        return carts.removeItem(id, productId);
    }

    @DeleteMapping("/{id}/items")
    public ResponseEntity<Void> clear(@PathVariable Long id) {
        carts.clear(id);
        return ResponseEntity.noContent().build();
    }
}
