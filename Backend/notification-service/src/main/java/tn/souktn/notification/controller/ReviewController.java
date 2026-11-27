package tn.souktn.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reviews", description = "Manage product reviews and ratings.")
public class ReviewController {
    private final ReviewService service;

    @PostMapping
    @Operation(summary = "Create a product review")
    public ResponseEntity<Review> create(@Valid @RequestBody Review review) {
        Review created = service.create(review);
        return ResponseEntity.created(URI.create("/api/reviews/" + created.getId())).body(created);
    }

    @GetMapping
    @Operation(summary = "List reviews for a product")
    public List<Review> findByProduct(@RequestParam String productId) {
        return service.findByProduct(productId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a review")
    public Review findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/product/{productId}/average")
    @Operation(summary = "Get average product rating")
    public Map<String, Double> getAverageRating(@PathVariable String productId) {
        return Map.of("average", service.getAverageRating(productId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product review")
    public Review update(@PathVariable Long id, @Valid @RequestBody Review review) {
        return service.update(id, review);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product review")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
