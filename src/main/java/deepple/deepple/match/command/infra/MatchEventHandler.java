package deepple.deepple.match.command.infra;

import deepple.deepple.match.command.application.match.MatchService;
import deepple.deepple.member.query.member.application.event.MemberProfileRetrievedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class MatchEventHandler {
    private final MatchService matchService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberProfileRetrievedEvent event) {
        if (event.getMatchRequesterId() == null || event.getMatchResponderId() == null) {
            return;
        }
        try {
            matchService.read(event.getRetrieverId(), event.getMatchRequesterId(), event.getMatchResponderId());
        } catch (Exception e) {
            log.error("매칭 메시지 읽음 처리 중 예외 발생. retrieverId: {}, requesterId: {}, responderId: {}",
                event.getRetrieverId(), event.getMatchRequesterId(), event.getMatchResponderId(), e);
        }
    }
}
