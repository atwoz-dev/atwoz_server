package deepple.deepple.notification.command.application;

import deepple.deepple.notification.command.domain.ChannelType;
import deepple.deepple.notification.command.domain.NotificationType;
import deepple.deepple.notification.command.domain.SenderType;
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
