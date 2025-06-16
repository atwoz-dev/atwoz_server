package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberDeleteScheduler {
    private final MemberCommandRepository memberCommandRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.cron}")
    public void delete() {
        LocalDateTime now = LocalDateTime.now();
        List<Long> memberIds = memberCommandRepository.findIdDeletedBefore(now.minusMonths(3));
        memberCommandRepository.deleteInIds(memberIds);
    }
}
