package atwoz.atwoz.match.command.domain.match;

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

    private Long requesterId;

    private Long responderId;

    private String requestMessage;

    private String responseMessage;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private MatchStatus status;

    public static Match of(@NonNull Long requesterId, @NonNull Long responderId, @NonNull String requestMessage) {
        return Match.builder()
                .requesterId(requesterId)
                .responderId(responderId)
                .requestMessage(requestMessage)
                .status(MatchStatus.WAITING)
                .build();
    }
}
