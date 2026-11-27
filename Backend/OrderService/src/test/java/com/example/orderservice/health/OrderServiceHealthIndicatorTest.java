package com.example.orderservice.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Status;
import org.springframework.dao.DataAccessResourceFailureException;

class OrderServiceHealthIndicatorTest {
    @Test
    void reportsDownWhenOrderRepositoryIsUnavailable() {
        OrderRepository orders = mock(OrderRepository.class);
        when(orders.count()).thenThrow(new DataAccessResourceFailureException("database unavailable"));

        assertEquals(
                Status.DOWN,
                new OrderServiceHealthIndicator(orders).health().getStatus());
    }
}
