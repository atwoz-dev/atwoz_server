package atwoz.atwoz.notification.command.application;

import lombok.NonNull;

import java.util.List;

public record NotificationReadRequest(
    @NonNull List<Long> notificationIds
) {
}