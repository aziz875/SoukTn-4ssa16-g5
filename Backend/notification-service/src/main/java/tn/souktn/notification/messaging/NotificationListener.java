package tn.souktn.notification.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tn.souktn.notification.entity.Notification;
import tn.souktn.notification.dto.CreateNotificationRequest;
import tn.souktn.notification.service.NotificationService;

@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

    @RabbitListener(queues = "${notification.queue.name:notification.events}")
    public void receive(NotificationEvent event) {
        CreateNotificationRequest request = new CreateNotificationRequest(
                event.getRecipientId(),
                event.getTitle(),
                event.getMessage(),
                event.getActionUrl(),
                null);
        notificationService.create(request);
    }
}
