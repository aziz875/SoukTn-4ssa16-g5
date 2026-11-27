package tn.souktn.notification.messaging;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationEvent {
    private String recipientId;
    private String title;
    private String message;
    private String actionUrl;
}
