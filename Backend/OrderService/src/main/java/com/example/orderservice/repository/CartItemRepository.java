package com.example.orderservice.repository;

import com.example.orderservice.entity.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Override
    @EntityGraph(attributePaths = "cart")
    List<CartItem> findAll();

    @Override
    @EntityGraph(attributePaths = "cart")
    Optional<CartItem> findById(Long id);
}
