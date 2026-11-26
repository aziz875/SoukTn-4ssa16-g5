package tn.souktn.delivery.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.souktn.delivery.entity.Delivery;
import tn.souktn.delivery.entity.DeliveryStatus;
import tn.souktn.delivery.entity.DeliveryTrackingEvent;
import tn.souktn.delivery.service.DeliveryService;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public ResponseEntity<Delivery> createDelivery(@Valid @RequestBody Delivery delivery) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.createDelivery(delivery));
    }

    @GetMapping
    public List<Delivery> getAllDeliveries() {
        return deliveryService.getAllDeliveries();
    }

    @GetMapping("/{id}")
    public Delivery getDeliveryById(@PathVariable Long id) {
        return deliveryService.getDeliveryById(id);
    }

    @GetMapping("/tracking/{trackingNumber}")
    public Delivery getByTrackingNumber(@PathVariable String trackingNumber) {
        return deliveryService.getByTrackingNumber(trackingNumber);
    }

    @GetMapping("/order/{orderId}")
    public List<Delivery> getDeliveriesByOrderId(@PathVariable Long orderId) {
        return deliveryService.getDeliveriesByOrderId(orderId);
    }

    @GetMapping("/driver/{driverId}")
    public List<Delivery> getDeliveriesByDriverId(@PathVariable Long driverId) {
        return deliveryService.getDeliveriesByDriverId(driverId);
    }

    @PutMapping("/{id}")
    public Delivery updateDelivery(@PathVariable Long id, @Valid @RequestBody Delivery delivery) {
        return deliveryService.updateDelivery(id, delivery);
    }

    @PatchMapping("/{id}/assign/{driverId}")
    public Delivery assignDriver(@PathVariable Long id, @PathVariable Long driverId) {
        return deliveryService.assignDriver(id, driverId);
    }

    @PatchMapping("/{id}/status")
    public Delivery updateStatus(@PathVariable Long id,
                                 @RequestParam DeliveryStatus status,
                                 @RequestParam(required = false) String location,
                                 @RequestParam(required = false) String notes) {
        return deliveryService.updateStatus(id, status, location, notes);
    }

    @GetMapping("/{id}/tracking-events")
    public List<DeliveryTrackingEvent> getTrackingHistory(@PathVariable Long id) {
        return deliveryService.getTrackingHistory(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
    }
}
