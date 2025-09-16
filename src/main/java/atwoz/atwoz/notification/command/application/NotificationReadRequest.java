package atwoz.atwoz.notification.command.application;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record NotificationReadRequest(
    @NotEmpty List<Long> notificationIds
) {
}