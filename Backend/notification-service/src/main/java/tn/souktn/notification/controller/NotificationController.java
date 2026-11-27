package tn.souktn.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.souktn.notification.entity.Notification;
import tn.souktn.notification.service.NotificationService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;

    @PostMapping
    public ResponseEntity<Notification> create(@Valid @RequestBody Notification notification) {
        Notification created = service.create(notification);
        return ResponseEntity.created(URI.create("/api/notifications/" + created.getId())).body(created);
    }

    @GetMapping
    public List<Notification> findByRecipient(@RequestParam String recipientId) {
        return service.findByRecipient(recipientId);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> countUnread(@RequestParam String recipientId) {
        return Map.of("count", service.countUnread(recipientId));
    }

    @PatchMapping("/{id}/read")
    public Notification markRead(@PathVariable Long id) {
        return service.markRead(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
