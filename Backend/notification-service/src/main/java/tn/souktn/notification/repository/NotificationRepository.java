package tn.souktn.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.notification.entity.Notification;
import tn.souktn.notification.entity.NotificationStatus;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
    long countByRecipientIdAndStatus(String recipientId, NotificationStatus status);
}
