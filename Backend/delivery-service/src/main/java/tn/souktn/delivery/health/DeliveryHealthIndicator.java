package tn.souktn.delivery.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import tn.souktn.delivery.repository.DeliveryRepository;
import tn.souktn.delivery.repository.DriverRepository;

@Component
public class DeliveryHealthIndicator implements HealthIndicator {

    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;

    public DeliveryHealthIndicator(DeliveryRepository deliveryRepository,
                                   DriverRepository driverRepository) {
        this.deliveryRepository = deliveryRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public Health health() {
        try {
            long totalDeliveries = deliveryRepository.count();
            long availableDrivers = driverRepository.countByAvailableTrue();
            return Health.up()
                    .withDetail("totalDeliveries", totalDeliveries)
                    .withDetail("availableDrivers", availableDrivers)
                    .build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
