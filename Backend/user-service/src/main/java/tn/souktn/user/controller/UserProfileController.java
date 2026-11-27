package tn.souktn.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.souktn.user.dto.CreateUserProfileRequest;
import tn.souktn.user.dto.UpdateUserProfileRequest;
import tn.souktn.user.entity.UserProfile;
import tn.souktn.user.service.UserProfileService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService service;

    @PostMapping
    public ResponseEntity<UserProfile> create(@Valid @RequestBody CreateUserProfileRequest request) {
        UserProfile created = service.create(request);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId())).body(created);
    }

    @GetMapping
    public List<UserProfile> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public UserProfile findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public UserProfile update(@PathVariable String id, @Valid @RequestBody UpdateUserProfileRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
