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
    private static final int RETRY_ATTEMPTS = 2; // 최초 1회 + 재시도 1회
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
            .slidingWindowSize(30)                                          // 30초 시간 윈도우
            .minimumNumberOfCalls(10)                                        // 최소 10회 호출 후 실패율 계산
            .failureRateThreshold(50f)                                      // 실패율 50% 이상시 차단
            .slowCallRateThreshold(50f)                                     // 느린 응답 50% 이상시 차단
            .slowCallDurationThreshold(Duration.ofMillis(500))              // 500ms 이상을 느린 응답으로 판단
            .waitDurationInOpenState(Duration.ofMillis(5000))               // OPEN 상태에서 5초 대기
            .automaticTransitionFromOpenToHalfOpenEnabled(true)             // HALF_OPEN 상태로 자동 전환
            .permittedNumberOfCallsInHalfOpenState(5)                       // HALF_OPEN에서 5회 테스트 호출 허용
            .maxWaitDurationInHalfOpenState(Duration.ofMillis(10000))        // HALF_OPEN에서 최대 10초 대기
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
