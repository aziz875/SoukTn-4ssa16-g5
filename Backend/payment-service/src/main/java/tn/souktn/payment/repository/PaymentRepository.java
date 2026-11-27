package tn.souktn.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.payment.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findByUserId(String userId);

    boolean existsByOrderId(Long orderId);
}
