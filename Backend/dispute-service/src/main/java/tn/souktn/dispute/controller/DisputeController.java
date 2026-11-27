package tn.souktn.dispute.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.souktn.dispute.entity.Dispute;
import tn.souktn.dispute.service.DisputeService;

import java.util.List;

@RestController
@RequestMapping("/api/disputes")
public class DisputeController {

    private final DisputeService service;

    public DisputeController(DisputeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Dispute> create(@Valid @RequestBody Dispute dispute) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dispute));
    }

    @GetMapping
    public List<Dispute> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Dispute findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Dispute update(@PathVariable Long id, @Valid @RequestBody Dispute dispute) {
        return service.update(id, dispute);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
