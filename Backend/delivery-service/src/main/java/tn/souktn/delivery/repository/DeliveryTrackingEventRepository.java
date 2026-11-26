package tn.souktn.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.delivery.entity.DeliveryTrackingEvent;

import java.util.List;

public interface DeliveryTrackingEventRepository extends JpaRepository<DeliveryTrackingEvent, Long> {
    List<DeliveryTrackingEvent> findByDeliveryIdOrderByEventTimeAsc(Long deliveryId);
}
