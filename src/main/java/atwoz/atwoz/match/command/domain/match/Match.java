package atwoz.atwoz.match.command.domain.match;

import atwoz.atwoz.match.command.domain.match.vo.Message;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "matches", indexes = {
        @Index(name = "idx_requester_id", columnList = "requesterId"),
        @Index(name = "idx_responder_id", columnList = "responderId")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private MatchStatus status;

    public static Match request(long requesterId, long responderId, @NonNull Message requestMessage) {
        /**
         * TODO : 매칭을 요청하는 경우, 하트 소비 이벤트 발행!
         */
        return Match.builder()
                .requesterId(requesterId)
                .responderId(responderId)
                .requestMessage(requestMessage)
                .status(MatchStatus.WAITING)
                .build();
    }
}
