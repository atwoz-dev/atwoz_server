package atwoz.atwoz.match.command.domain.match;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.match.command.domain.match.event.MatchRequestCompletedEvent;
import atwoz.atwoz.match.command.domain.match.event.MatchRequestedEvent;
import atwoz.atwoz.match.command.domain.match.event.MatchRespondedEvent;
import atwoz.atwoz.match.command.domain.match.exception.InvalidMatchStatusChangeException;
import atwoz.atwoz.match.command.domain.match.vo.Message;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "matches", indexes = {
        @Index(name = "idx_responder_id", columnList = "responderId"),
        @Index(name = "idx_requester_id_responder_id", columnList = "requesterId, responderId")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    private Long requesterId;

    @Getter
    private Long responderId;

    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "request_message"))
    })
    private Message requestMessage;

    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "response_message"))
    })
    private Message responseMessage;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private MatchStatus status;

    public void approve(@NonNull Message message) {
        validateChangeStatus();
        status = MatchStatus.MATCHED;
        responseMessage = message;
        Events.raise(MatchRespondedEvent.of(requesterId, responderId, status));
    }

    public void reject() {
        validateChangeStatus();
        status = MatchStatus.REJECTED;
        Events.raise(MatchRespondedEvent.of(requesterId, responderId, status));
    }

    public void expire() {
        validateChangeStatus();
        status = MatchStatus.EXPIRED;
        Events.raise(MatchRespondedEvent.of(requesterId, responderId, status));
    }

    public void checkRejected() {
        validateChangeRejectChecked();
        status = MatchStatus.REJECT_CHECKED;
    }

    public static Match request(long requesterId, long responderId, @NonNull Message requestMessage) {
        Events.raise(MatchRequestedEvent.of(requesterId, responderId));
        Events.raise(MatchRequestCompletedEvent.of(requesterId, responderId));

        return Match.builder()
                .requesterId(requesterId)
                .responderId(responderId)
                .requestMessage(requestMessage)
                .status(MatchStatus.WAITING)
                .build();
    }

    private void validateChangeStatus() {
        if (status != MatchStatus.WAITING)
            throw new InvalidMatchStatusChangeException();
    }

    private void validateChangeRejectChecked() {
        if (status != MatchStatus.REJECTED)
            throw new InvalidMatchStatusChangeException();
    }
}
