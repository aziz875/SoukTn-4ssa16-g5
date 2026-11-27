package com.example.orderservice.repository;

import com.example.orderservice.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderNumber(String orderNumber);

    @Override
    @EntityGraph(attributePaths = "items")
    List<Order> findAll();

    @Override
    @EntityGraph(attributePaths = "items")
    Optional<Order> findById(Long id);
}
