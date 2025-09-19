package atwoz.atwoz.payment.command.infra.order;

import atwoz.atwoz.common.config.ResiliencePolicyConfigurer;
import atwoz.atwoz.payment.command.infra.order.exception.AppStoreClientException;
import atwoz.atwoz.payment.command.infra.order.exception.InvalidAppReceiptException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AppStoreQueryResilienceConfig implements ResiliencePolicyConfigurer {
    public static final String RETRY_POLICY_NAME = "appStoreQueryRetry";
    public static final String CIRCUIT_BREAKER_POLICY_NAME = "appStoreQueryCircuitBreaker";
    private static final int RETRY_ATTEMPTS = 1;
    private static final int RETRY_WAIT_DURATION_MILLIS = 200;

    @Override
    public void configure(RetryRegistry r, CircuitBreakerRegistry c) {
        r.retry(RETRY_POLICY_NAME, retryConfig());
        c.circuitBreaker(CIRCUIT_BREAKER_POLICY_NAME, circuitBreakerConfig());
    }

    private RetryConfig retryConfig() {
        IntervalFunction intervalFunction =
            IntervalFunction.ofExponentialRandomBackoff(
                Duration.ofMillis(RETRY_WAIT_DURATION_MILLIS), // 초기 지연 200ms
                2.0,      // 2배씩 증가
                0.3       // ±30% 지터
            );

        return RetryConfig.custom()
            .maxAttempts(RETRY_ATTEMPTS)
            .intervalFunction(intervalFunction)
            .ignoreExceptions(
                FeignException.BadRequest.class,        // 400: 잘못된 요청
                FeignException.NotFound.class,          // 404: 트랜잭션 없음
                InvalidAppReceiptException.class,       // 잘못된 영수증
                AppStoreClientException.class           // 기타 클라이언트 예외 (재시도로 해결 불가)
            )
            .retryExceptions(
                FeignException.Unauthorized.class,          // 401: 인증 실패 (토큰 재발급 후 재시도)
                FeignException.TooManyRequests.class,       // 429: 요청 한도 초과
                FeignException.InternalServerError.class,   // 500: 서버 오류
                feign.RetryableException.class              // connection timeout, read timeout 등
            )
            .build();
    }

    private CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
            .slidingWindowSize(10)
            .minimumNumberOfCalls(4)
            .failureRateThreshold(50f)
            .slowCallRateThreshold(50f)
            .slowCallDurationThreshold(Duration.ofMillis(100))
            .waitDurationInOpenState(Duration.ofMillis(2000))
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .permittedNumberOfCallsInHalfOpenState(2)
            .maxWaitDurationInHalfOpenState(Duration.ofMillis(3000))
            .recordExceptions(
                FeignException.TooManyRequests.class,       // 429: 요청 한도 초과
                FeignException.InternalServerError.class,   // 500: 서버 오류
                feign.RetryableException.class              // connection timeout, read timeout 등
            )
            .ignoreExceptions(
                FeignException.BadRequest.class,            // 400: 잘못된 요청
                FeignException.Unauthorized.class,          // 401: 인증 실패 (토큰 재발급 후 재시도)
                FeignException.NotFound.class               // 404: 트랜잭션 없음
            )
            .build();
    }
}