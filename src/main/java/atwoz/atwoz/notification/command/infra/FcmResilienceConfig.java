package atwoz.atwoz.notification.command.infra;

import atwoz.atwoz.common.config.ResiliencePolicyConfigurer;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Configuration;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Duration;

@Configuration
public class FcmResilienceConfig implements ResiliencePolicyConfigurer {

    public static final String RETRY_POLICY_NAME = "fcmRetry";
    public static final String CIRCUIT_BREAKER_POLICY_NAME = "fcmCircuitBreaker";
    private static final int MAX_ATTEMPTS = 2;  // 최초 1회 + 재시도 1회
    private static final int RETRY_WAIT_DURATION_MILLIS = 500;

    @Override
    public void configure(RetryRegistry r, CircuitBreakerRegistry c) {
        r.retry(RETRY_POLICY_NAME, retryConfig());
        c.circuitBreaker(CIRCUIT_BREAKER_POLICY_NAME, circuitBreakerConfig());
    }

    private RetryConfig retryConfig() {
        return RetryConfig.custom()
            .maxAttempts(MAX_ATTEMPTS)
            .intervalFunction(
                IntervalFunction.ofExponentialRandomBackoff(
                    Duration.ofMillis(RETRY_WAIT_DURATION_MILLIS),  // 초기 지연 500ms
                    2.0,  // 2배씩 증가
                    0.3   // ±30% 지터
                )
            )
            .ignoreExceptions(  // 재시도하면 안 되는 예외들
                IllegalArgumentException.class,   // 잘못된 토큰, 메시지 형식 등
                SocketTimeoutException.class,     // READ_TIMEOUT (요청은 처리되었을 가능성 있음)
                FirebaseMessagingException.class  // FCM 응답 관련 모든 예외 (중복 알림 방지)
            )
            .retryExceptions(  // 연결조차 안 된 경우 재시도 (안전한 경우)
                ConnectException.class,           // 연결 실패
                UnknownHostException.class        // DNS 실패
            )
            .build();
    }

    private CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
            .slidingWindowSize(60)                                    // 60초 시간 윈도우
            .minimumNumberOfCalls(5)                                  // 최소 5회 호출 후 실패율 계산
            .failureRateThreshold(50f)                                // 실패율 50% 이상시 차단
            .slowCallRateThreshold(50f)                               // 느린 응답 50% 이상시 차단
            .slowCallDurationThreshold(Duration.ofMillis(2000))       // 2초 이상을 느린 응답으로 판단
            .waitDurationInOpenState(Duration.ofMillis(7000))         // OPEN 상태에서 7초 대기
            .automaticTransitionFromOpenToHalfOpenEnabled(true)  // HALF_OPEN 상태로 자동 전환
            .permittedNumberOfCallsInHalfOpenState(3)                 // HALF_OPEN에서 3회 테스트 호출 허용
            .maxWaitDurationInHalfOpenState(Duration.ofMillis(5000))  // HALF_OPEN에서 최대 5초 대기
            .recordExceptions(  // 외부 시스템 장애
                FirebaseMessagingException.class,  // FCM 서버 장애
                SocketTimeoutException.class,      // 네트워크 타임아웃
                ConnectException.class,            // 연결 실패
                UnknownHostException.class         // DNS 실패
            )
            .ignoreExceptions(IllegalArgumentException.class)
            .build();
    }
}