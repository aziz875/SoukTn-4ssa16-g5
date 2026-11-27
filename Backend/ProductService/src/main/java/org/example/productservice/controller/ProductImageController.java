package org.example.productservice.controller;

import org.example.productservice.entity.ProductImage;
import org.example.productservice.repository.ProductImageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "ProductImage")
public class ProductImageController {

    private final ProductImageRepository repository;

    @PostMapping
    @Operation(summary = "Create image")
    public ResponseEntity<ProductImage> create(@RequestBody ProductImage image) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(image));
    }

    @GetMapping
    @Operation(summary = "Get all images")
    public ResponseEntity<List<ProductImage>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get image by ID")
    public ResponseEntity<ProductImage> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update image")
    public ResponseEntity<ProductImage> update(@PathVariable Long id, @RequestBody ProductImage image) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setUrl(image.getUrl());
                    existing.setPosition(image.getPosition());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete image")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
