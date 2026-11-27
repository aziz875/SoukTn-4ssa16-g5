package tn.souktn.notification.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.souktn.notification.user.dto.CreateUserProfileRequest;
import tn.souktn.notification.user.dto.UpdateUserProfileRequest;
import tn.souktn.notification.user.entity.UserProfile;
import tn.souktn.notification.user.service.UserProfileService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profiles", description = "Manage user profiles linked to Keycloak IDs.")
public class UserProfileController {
    private final UserProfileService service;

    @PostMapping
    @Operation(summary = "Create a user profile")
    public ResponseEntity<UserProfile> create(@Valid @RequestBody CreateUserProfileRequest request) {
        UserProfile created = service.create(request);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId())).body(created);
    }

    @GetMapping
    @Operation(summary = "List user profiles")
    public List<UserProfile> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user profile by Keycloak ID")
    public UserProfile findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user profile")
    public UserProfile update(@PathVariable String id, @Valid @RequestBody UpdateUserProfileRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user profile")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
