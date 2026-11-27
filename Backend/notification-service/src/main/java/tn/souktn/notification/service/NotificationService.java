package tn.souktn.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.souktn.notification.dto.CreateNotificationRequest;
import tn.souktn.notification.entity.Notification;
import tn.souktn.notification.entity.NotificationChannel;
import tn.souktn.notification.entity.NotificationStatus;
import tn.souktn.notification.exception.ResourceNotFoundException;
import tn.souktn.notification.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;

    @Transactional
    public Notification create(CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setRecipientId(request.recipientId());
        notification.setTitle(request.title());
        notification.setMessage(request.message());
        notification.setActionUrl(request.actionUrl());
        notification.setChannel(request.channel() == null ? NotificationChannel.IN_APP : request.channel());
        return repository.save(notification);
    }

    public List<Notification> findByRecipient(String recipientId) {
        return repository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    public long countUnread(String recipientId) {
        return repository.countByRecipientIdAndStatus(recipientId, NotificationStatus.UNREAD);
    }

    @Transactional
    public Notification markRead(Long id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        return repository.save(notification);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found: " + id);
        }
        repository.deleteById(id);
    }
}
