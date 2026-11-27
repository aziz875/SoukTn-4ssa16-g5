package tn.souktn.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.souktn.notification.entity.Review;
import tn.souktn.notification.service.ReviewService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody Review review) {
        Review created = service.create(review);
        return ResponseEntity.created(URI.create("/api/reviews/" + created.getId())).body(created);
    }

    @GetMapping
    public List<Review> findByProduct(@RequestParam String productId) {
        return service.findByProduct(productId);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/product/{productId}/average")
    public Map<String, Double> getAverageRating(@PathVariable String productId) {
        return Map.of("average", service.getAverageRating(productId));
    }

    @PutMapping("/{id}")
    public Review update(@PathVariable Long id, @Valid @RequestBody Review review) {
        return service.update(id, review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
