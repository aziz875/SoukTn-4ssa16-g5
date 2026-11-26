package tn.souktn.delivery.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.souktn.delivery.entity.Delivery;
import tn.souktn.delivery.entity.DeliveryStatus;
import tn.souktn.delivery.entity.DeliveryTrackingEvent;
import tn.souktn.delivery.entity.Driver;
import tn.souktn.delivery.exception.ResourceNotFoundException;
import tn.souktn.delivery.repository.DeliveryRepository;
import tn.souktn.delivery.repository.DeliveryTrackingEventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryTrackingEventRepository trackingEventRepository;
    private final DriverService driverService;

    public DeliveryService(DeliveryRepository deliveryRepository,
                           DeliveryTrackingEventRepository trackingEventRepository,
                           DriverService driverService) {
        this.deliveryRepository = deliveryRepository;
        this.trackingEventRepository = trackingEventRepository;
        this.driverService = driverService;
    }

    public Delivery createDelivery(Delivery delivery) {
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.addTrackingEvent(new DeliveryTrackingEvent(
                DeliveryStatus.PENDING,
                delivery.getCity(),
                "Delivery request created"
        ));
        return deliveryRepository.save(delivery);
    }

    @Transactional(readOnly = true)
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Delivery getDeliveryById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Delivery getByTrackingNumber(String trackingNumber) {
        return deliveryRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with tracking number: " + trackingNumber));
    }

    @Transactional(readOnly = true)
    public List<Delivery> getDeliveriesByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public List<Delivery> getDeliveriesByDriverId(Long driverId) {
        return deliveryRepository.findByDriverId(driverId);
    }

    public Delivery updateDelivery(Long id, Delivery updated) {
        Delivery existing = getDeliveryById(id);
        existing.setCustomerName(updated.getCustomerName());
        existing.setCustomerPhone(updated.getCustomerPhone());
        existing.setShippingAddress(updated.getShippingAddress());
        existing.setCity(updated.getCity());
        existing.setEstimatedDeliveryDate(updated.getEstimatedDeliveryDate());
        return deliveryRepository.save(existing);
    }

    public Delivery assignDriver(Long deliveryId, Long driverId) {
        Delivery delivery = getDeliveryById(deliveryId);
        Driver driver = driverService.getDriverById(driverId);

        delivery.setDriver(driver);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.addTrackingEvent(new DeliveryTrackingEvent(
                DeliveryStatus.ASSIGNED,
                delivery.getCity(),
                "Assigned to driver: " + driver.getFullName()
        ));
        return deliveryRepository.save(delivery);
    }

    public Delivery updateStatus(Long deliveryId, DeliveryStatus newStatus, String location, String notes) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setStatus(newStatus);
        if (newStatus == DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(LocalDateTime.now());
        }
        delivery.addTrackingEvent(new DeliveryTrackingEvent(
                newStatus,
                (location != null && !location.isBlank()) ? location : delivery.getCity(),
                notes
        ));
        return deliveryRepository.save(delivery);
    }

    @Transactional(readOnly = true)
    public List<DeliveryTrackingEvent> getTrackingHistory(Long deliveryId) {
        getDeliveryById(deliveryId);
        return trackingEventRepository.findByDeliveryIdOrderByEventTimeAsc(deliveryId);
    }

    public void deleteDelivery(Long id) {
        Delivery existing = getDeliveryById(id);
        deliveryRepository.delete(existing);
    }
}
