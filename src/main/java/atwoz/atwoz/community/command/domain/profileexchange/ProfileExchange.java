package atwoz.atwoz.community.command.domain.profileexchange;


import atwoz.atwoz.common.entity.BaseEntity;
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

    private ProfileExchangeStatus status;

    private ProfileExchange(long requesterId, long responderId, @NonNull ProfileExchangeStatus status) {
        validateRequesterIdAndResponderId(requesterId, responderId);
        this.requesterId = requesterId;
        this.responderId = responderId;
        this.status = status;
    }

    public static ProfileExchange request(long requesterId, long responderId) {
        return new ProfileExchange(requesterId, responderId, ProfileExchangeStatus.WAITING);
    }

    private void validateRequesterIdAndResponderId(long requesterId, long responderId) {
        if (requesterId == responderId) {
            throw new SelfProfileExchangeException();
        }
    }
}
