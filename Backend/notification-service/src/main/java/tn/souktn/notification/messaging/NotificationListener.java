package tn.souktn.notification.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tn.souktn.notification.entity.Notification;
import tn.souktn.notification.service.NotificationService;

@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

    @RabbitListener(queues = "${notification.queue.name:notification.events}")
    public void receive(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setRecipientId(event.getRecipientId());
        notification.setTitle(event.getTitle());
        notification.setMessage(event.getMessage());
        notification.setActionUrl(event.getActionUrl());
        notificationService.create(notification);
    }
}
