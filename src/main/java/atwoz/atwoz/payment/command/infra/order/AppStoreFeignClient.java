package atwoz.atwoz.payment.command.infra.order;

import feign.Request;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.Duration;

@FeignClient(
    name = "appStoreApiClient",
    url = "${payment.app-store.base-url}",
    configuration = AppStoreFeignClient.Config.class
)
public interface AppStoreFeignClient {

    @GetMapping("/inApps/v1/transactions/{transactionId}")
    AppStoreTransactionResponse getTransactionInfo(
        @PathVariable("transactionId") String transactionId,
        @RequestHeader("Authorization") String authorization
    );

    class Config {
        private static final int CONNECT_TIMEOUT_MILLIS = 1000;
        private static final int READ_TIMEOUT_MILLIS = 2000;
        private static final boolean FOLLOW_REDIRECTS = false;

        @Bean
        public feign.Retryer retryer() {
            return feign.Retryer.NEVER_RETRY; // resilience4j에서 처리
        }

        @Bean
        public Request.Options options() {
            return new Request.Options(
                Duration.ofMillis(CONNECT_TIMEOUT_MILLIS),
                Duration.ofMillis(READ_TIMEOUT_MILLIS),
                FOLLOW_REDIRECTS
            );
        }
    }
}