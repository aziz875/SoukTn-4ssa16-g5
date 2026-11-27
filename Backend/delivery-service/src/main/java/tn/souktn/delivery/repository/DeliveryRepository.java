package tn.souktn.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.delivery.entity.Delivery;
import tn.souktn.delivery.entity.DeliveryStatus;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByTrackingNumber(String trackingNumber);
    List<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByDriverId(Long driverId);
    List<Delivery> findByStatus(DeliveryStatus status);
}
