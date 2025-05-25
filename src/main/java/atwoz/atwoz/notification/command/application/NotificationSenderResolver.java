package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.ChannelType;
import atwoz.atwoz.notification.command.domain.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationSenderResolver {

    private final List<NotificationSender> senders;

    public Optional<NotificationSender> resolve(ChannelType channelType) {
        return senders.stream()
            .filter(sender -> sender.channel() == channelType)
            .findFirst();
    }
}