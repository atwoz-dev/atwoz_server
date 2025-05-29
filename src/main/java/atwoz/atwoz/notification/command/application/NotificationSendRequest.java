package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.ChannelType;
import atwoz.atwoz.notification.command.domain.NotificationType;
import atwoz.atwoz.notification.command.domain.SenderType;
import lombok.NonNull;

import java.util.Map;

public record NotificationSendRequest(
    @NonNull SenderType senderType,
    long senderId,
    long receiverId,
    @NonNull NotificationType notificationType,
    @NonNull Map<String, String> params,
    @NonNull ChannelType channelType
) {
}
