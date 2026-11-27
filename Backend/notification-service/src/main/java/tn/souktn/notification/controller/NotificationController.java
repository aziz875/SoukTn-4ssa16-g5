package tn.souktn.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.souktn.notification.entity.Notification;
import tn.souktn.notification.dto.CreateNotificationRequest;
import tn.souktn.notification.service.NotificationService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Create and manage recipient notifications.")
public class NotificationController {
    private final NotificationService service;

    @PostMapping
    @Operation(summary = "Create a notification")
    public ResponseEntity<Notification> create(@Valid @RequestBody CreateNotificationRequest request) {
        Notification created = service.create(request);
        return ResponseEntity.created(URI.create("/api/notifications/" + created.getId())).body(created);
    }

    @GetMapping
    @Operation(summary = "List notifications for a recipient")
    public List<Notification> findByRecipient(@RequestParam String recipientId) {
        return service.findByRecipient(recipientId);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Count unread notifications for a recipient")
    public Map<String, Long> countUnread(@RequestParam String recipientId) {
        return Map.of("count", service.countUnread(recipientId));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public Notification markRead(@PathVariable Long id) {
        return service.markRead(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
