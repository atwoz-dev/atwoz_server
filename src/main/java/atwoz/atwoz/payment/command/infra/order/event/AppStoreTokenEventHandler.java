package atwoz.atwoz.payment.command.infra.order.event;

import atwoz.atwoz.payment.command.infra.order.AppStoreTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AppStoreTokenEventHandler {

    private final AppStoreTokenService appStoreTokenService;

    @Async
    @EventListener
    public void handleTokenExpired(AppStoreTokenExpiredEvent ignored) {
        appStoreTokenService.refreshToken();
    }
}
