package tn.souktn.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tn.souktn.notification.entity.NotificationChannel;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateNotificationRequest(
        @NotBlank @Size(max = 255) String recipientId,
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 2000) String message,
        @Size(max = 2048) String actionUrl,
        NotificationChannel channel) {
}
