package atwoz.atwoz.notification.query;

import atwoz.atwoz.notification.command.domain.NotificationPreference;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static atwoz.atwoz.notification.command.domain.QNotificationPreference.notificationPreference;

@Repository
@RequiredArgsConstructor
public class NotificationPreferenceQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<NotificationPreferenceView> findByMemberId(long memberId) {
        NotificationPreference preference = queryFactory
            .selectFrom(notificationPreference)
            .where(notificationPreference.memberId.eq(memberId))
            .fetchOne();

        return Optional.ofNullable(preference)
            .map(this::toView);
    }

    private NotificationPreferenceView toView(NotificationPreference preference) {
        return new NotificationPreferenceView(
            preference.getMemberId(),
            preference.isEnabledGlobally(),
            preference.getNotificationPreferences()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey().name(), Map.Entry::getValue))
        );
    }
}