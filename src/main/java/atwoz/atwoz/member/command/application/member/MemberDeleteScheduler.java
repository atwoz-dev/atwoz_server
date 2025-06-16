package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberDeleteScheduler {
    private final MemberCommandRepository memberCommandRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.cron}")
    public void delete() {
        LocalDateTime now = LocalDateTime.now();
        memberCommandRepository.deleteBefore(now.minusMonths(3));
    }
}
