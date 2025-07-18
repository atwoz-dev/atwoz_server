package atwoz.atwoz.community.command.domain.profileexchange;


import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.community.command.domain.profileexchange.event.ProfileExchangeAcceptedEvent;
import atwoz.atwoz.community.command.domain.profileexchange.event.ProfileExchangeRejectedEvent;
import atwoz.atwoz.community.command.domain.profileexchange.event.ProfileExchangeRequestedEvent;
import atwoz.atwoz.community.command.domain.profileexchange.exception.InvalidProfileExchangeStatusException;
import atwoz.atwoz.community.command.domain.profileexchange.exception.SelfProfileExchangeException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profile_exchanges", indexes = {
    @Index(name = "idx_responder_id", columnList = "responderId"),
    @Index(name = "idx_requester_id_responder_id", columnList = "requesterId, responderId")
})
public class ProfileExchange extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long requesterId;

    private long responderId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private ProfileExchangeStatus status;

    private ProfileExchange(long requesterId, long responderId, @NonNull ProfileExchangeStatus status) {
        validateRequesterIdAndResponderId(requesterId, responderId);
        this.requesterId = requesterId;
        this.responderId = responderId;
        this.status = status;
    }

    public static ProfileExchange request(long requesterId, long responderId, String senderName) {
        Events.raise(ProfileExchangeRequestedEvent.of(requesterId, responderId, senderName));
        return new ProfileExchange(requesterId, responderId, ProfileExchangeStatus.WAITING);
    }

    public void approve(String senderName) {
        validateWaitingStatus();
        Events.raise(ProfileExchangeAcceptedEvent.of(requesterId, responderId, senderName));
        status = ProfileExchangeStatus.APPROVE;
    }

    public void reject(String senderName) {
        validateWaitingStatus();
        Events.raise(ProfileExchangeRejectedEvent.of(requesterId, responderId, senderName));
        status = ProfileExchangeStatus.REJECTED;
    }

    private void validateRequesterIdAndResponderId(long requesterId, long responderId) {
        if (requesterId == responderId) {
            throw new SelfProfileExchangeException();
        }
    }

    private void validateWaitingStatus() {
        if (status != ProfileExchangeStatus.WAITING) {
            throw new InvalidProfileExchangeStatusException("대기상태의 요청에만 응답할 수 있습니다.");
        }
    }
}
