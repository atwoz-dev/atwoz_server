package deepple.deepple.payment.command.infra.order.event;

import deepple.deepple.common.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppStoreTokenExpiredEvent extends Event {
    private final String tokenKey;
}
