package atwoz.atwoz.payment.command.infra.order.event;

import atwoz.atwoz.common.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppStoreTokenExpiredEvent extends Event {
    private final String tokenKey;
}