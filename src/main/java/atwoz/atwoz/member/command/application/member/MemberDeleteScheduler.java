package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.command.domain.member.event.MembersDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// TODO : 각 도메인별 MembersDeletedEvent 핸들링 필요.

@Service
@RequiredArgsConstructor
public class MemberDeleteScheduler {
    /**
     * 삭제 대상이 된 멤버와 관련된 데이터를 삭제합니다.
     * 1. 멤버 데이터.
     * 2. 좋아요 데이터.
     * 3. 매칭 데이터.
     * 4. 셀프 소개 데이터.
     * 5. 하트 트랜잭션 데이터.
     * 6. 인터뷰 답변 데이터.
     * 7. 이상형 데이터.
     */
    private final MemberCommandRepository memberCommandRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void delete() {
        LocalDateTime now = LocalDateTime.now();
        List<Long> memberIds = findMemberIdsDeletedTargetMember(now.minusMonths(3));
        memberCommandRepository.deleteInIds(memberIds);
        Events.raise(MembersDeletedEvent.from(memberIds));
    }
    
    private List<Long> findMemberIdsDeletedTargetMember(LocalDateTime threeMonthAgo) {
        return memberCommandRepository.findAllDeletedBefore(threeMonthAgo).stream().map(Member::getId).toList();
    }
}
