package tn.souktn.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.souktn.payment.entity.Payment;
import tn.souktn.payment.entity.PaymentStatus;
import tn.souktn.payment.service.PaymentService;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    public ResponseEntity<Payment> create(@Valid @RequestBody Payment payment) {
        Payment created = service.create(payment);
        return ResponseEntity.created(URI.create("/api/payments/" + created.getId())).body(created);
    }

    @GetMapping
    public List<Payment> findAll(@RequestParam(required = false) String userId) {
        return service.findAll(userId);
    }

    @GetMapping("/{id}")
    public Payment findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/order/{orderId}")
    public Payment findByOrderId(@PathVariable Long orderId) {
        return service.findByOrderId(orderId);
    }

    @PutMapping("/{id}")
    public Payment update(@PathVariable Long id, @Valid @RequestBody Payment payment) {
        return service.update(id, payment);
    }

    @PatchMapping("/{id}/status")
    public Payment updateStatus(@PathVariable Long id, @RequestParam PaymentStatus status) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}