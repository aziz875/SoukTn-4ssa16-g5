package com.example.orderservice.service.impl;

import com.example.orderservice.api.dto.CartItemRequest;
import com.example.orderservice.api.dto.CartItemResponse;
import com.example.orderservice.api.dto.CartResponse;
import com.example.orderservice.api.dto.CreateCartRequest;
import com.example.orderservice.api.dto.UpdateCartRequest;
import com.example.orderservice.entity.Cart;
import com.example.orderservice.entity.CartItem;
import com.example.orderservice.repository.CartRepository;
import com.example.orderservice.service.CartService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository carts;

    public CartServiceImpl(CartRepository carts) {
        this.carts = carts;
    }

    @Override
    public CartResponse create(CreateCartRequest request) {
        return toResponse(carts.save(new Cart(request.userId())));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CartResponse> findAll() {
        return carts.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CartResponse findById(Long id) {
        return toResponse(findCart(id));
    }

    @Override
    public CartResponse update(Long id, UpdateCartRequest request) {
        Cart cart = findCart(id);
        cart.setUserId(request.userId());
        cart.clear();
        request.items().forEach(item -> cart.addItem(item.productId(), item.quantity()));
        return toResponse(cart);
    }

    @Override
    public CartResponse addItem(Long id, CartItemRequest request) {
        Cart cart = findCart(id);
        cart.addItem(request.productId(), request.quantity());
        return toResponse(cart);
    }

    @Override
    public CartResponse removeItem(Long id, Long productId) {
        Cart cart = findCart(id);
        if (!cart.removeItem(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit absent du panier");
        }
        return toResponse(cart);
    }

    @Override
    public void clear(Long id) {
        findCart(id).clear();
    }

    @Override
    public void delete(Long id) {
        carts.delete(findCart(id));
    }

    private Cart findCart(Long id) {
        return carts.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Panier introuvable"));
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();
        return new CartResponse(cart.getId(), cart.getUserId(), cart.getUpdatedAt(), items);
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(item.getId(), item.getCart().getId(), item.getProductId(), item.getQuantity());
    }
}
