package atwoz.atwoz.notification.command.infra;

import atwoz.atwoz.common.config.ResiliencePolicyConfigurer;
import atwoz.atwoz.notification.command.application.FcmException;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
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

    // Retry 설정값
    private static final int MAX_ATTEMPTS = 3;                 // 최초 1회 + 재시도 2회
    private static final int RETRY_INITIAL_WAIT_MILLIS = 500;
    private static final double RETRY_MULTIPLIER = 2.0;        // 지수 백오프 배수
    private static final double RETRY_JITTER = 0.3;            // ±30% 지터

    // Circuit Breaker 설정값
    private static final int CB_SLIDING_WINDOW_SIZE = 60;               // 60초 시간 윈도우
    private static final int CB_MINIMUM_NUMBER_OF_CALLS = 10;           // 최소 10회 호출 후 실패율 계산
    private static final float CB_FAILURE_RATE_THRESHOLD = 50f;         // 실패율 50% 이상시 차단
    private static final float CB_SLOW_CALL_RATE_THRESHOLD = 50f;       // 느린 응답 50% 이상시 차단
    private static final int CB_SLOW_CALL_DURATION_MILLIS = 2000;       // 2초 이상을 느린 응답으로 판단
    private static final int CB_WAIT_DURATION_IN_OPEN_MILLIS = 7000;    // OPEN 상태에서 7초 대기
    private static final int CB_PERMITTED_CALLS_IN_HALF_OPEN = 3;       // HALF_OPEN에서 3회 테스트 호출
    private static final int CB_MAX_WAIT_IN_HALF_OPEN_MILLIS = 5000;    // HALF_OPEN에서 최대 5초 대기

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
                    Duration.ofMillis(RETRY_INITIAL_WAIT_MILLIS),
                    RETRY_MULTIPLIER,
                    RETRY_JITTER
                )
            )
            .retryOnException(this::shouldRetry)
            .build();
    }

    private boolean shouldRetry(Throwable throwable) {
        Throwable exception = unwrapException(throwable);

        // 네트워크 연결 실패: 안전하게 재시도 가능
        if (exception instanceof ConnectException || exception instanceof UnknownHostException) {
            return true;
        }

        // READ_TIMEOUT: 요청이 이미 처리되었을 가능성이 있어 재시도하지 않음
        if (exception instanceof SocketTimeoutException) {
            return false;
        }

        // FirebaseMessagingException: 에러 코드에 따라 재시도 여부 결정
        if (exception instanceof FirebaseMessagingException fme) {
            MessagingErrorCode errorCode = fme.getMessagingErrorCode();
            if (errorCode == null) {
                return false;
            }

            return switch (errorCode) {
                // FCM 서버 오류: 재시도 가능
                case INTERNAL -> true;                  // FCM 내부 서버 오류
                case UNAVAILABLE -> true;               // FCM 서비스 일시 중단
                case QUOTA_EXCEEDED -> true;            // 할당량 초과 (잠시 후 복구 가능)

                // 클라이언트 오류: 재시도 불가능
                case INVALID_ARGUMENT -> false;         // 잘못된 요청 파라미터
                case UNREGISTERED -> false;             // 등록되지 않은 토큰
                case SENDER_ID_MISMATCH -> false;       // Sender ID 불일치
                case THIRD_PARTY_AUTH_ERROR -> false;   // 인증 오류

                // 기타 에러: 안전을 위해 재시도하지 않음
                default -> false;
            };
        }

        return false;
    }

    private CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
            .slidingWindowSize(CB_SLIDING_WINDOW_SIZE)
            .minimumNumberOfCalls(CB_MINIMUM_NUMBER_OF_CALLS)
            .failureRateThreshold(CB_FAILURE_RATE_THRESHOLD)
            .slowCallRateThreshold(CB_SLOW_CALL_RATE_THRESHOLD)
            .slowCallDurationThreshold(Duration.ofMillis(CB_SLOW_CALL_DURATION_MILLIS))
            .waitDurationInOpenState(Duration.ofMillis(CB_WAIT_DURATION_IN_OPEN_MILLIS))
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .permittedNumberOfCallsInHalfOpenState(CB_PERMITTED_CALLS_IN_HALF_OPEN)
            .maxWaitDurationInHalfOpenState(Duration.ofMillis(CB_MAX_WAIT_IN_HALF_OPEN_MILLIS))
            .recordException(this::shouldRecordAsFailure)
            .ignoreExceptions(IllegalArgumentException.class)
            .build();
    }

    /**
     * Circuit Breaker 실패로 기록할 예외인지 판단합니다.
     * <p>
     * 외부 시스템(FCM 서버, 네트워크) 장애만 실패로 기록하여 Circuit을 열도록 합니다.
     * 클라이언트 오류(잘못된 토큰, 파라미터 등)는 무시하여 정상 요청이 차단되지 않도록 합니다.
     */
    private boolean shouldRecordAsFailure(Throwable throwable) {
        Throwable exception = unwrapException(throwable);

        // 네트워크 장애: Circuit Breaker 실패로 기록 (외부 시스템 문제)
        if (exception instanceof ConnectException
            || exception instanceof UnknownHostException
            || exception instanceof SocketTimeoutException) {
            return true;
        }

        // FirebaseMessagingException: 에러 코드에 따라 결정
        if (exception instanceof FirebaseMessagingException fme) {
            MessagingErrorCode errorCode = fme.getMessagingErrorCode();
            if (errorCode == null) {
                return true;
            }

            return switch (errorCode) {
                // FCM 서버 오류
                case INTERNAL -> true;                  // FCM 내부 서버 오류
                case UNAVAILABLE -> true;               // FCM 서비스 일시 중단
                case QUOTA_EXCEEDED -> true;            // 할당량 초과

                // 클라이언트 오류 무시
                case INVALID_ARGUMENT -> false;         // 잘못된 요청 파라미터
                case UNREGISTERED -> false;             // 등록되지 않은 토큰 (정상 동작)
                case SENDER_ID_MISMATCH -> false;       // Sender ID 불일치
                case THIRD_PARTY_AUTH_ERROR -> false;   // 인증 오류

                // 기타 에러: 보수적으로 실패로 기록
                default -> true;
            };
        }

        return true;
    }

    private Throwable unwrapException(Throwable throwable) {
        if (throwable instanceof FcmException fcmEx) {
            return fcmEx.getCause();
        }
        return throwable;
    }
}