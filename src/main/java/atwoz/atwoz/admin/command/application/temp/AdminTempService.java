package atwoz.atwoz.admin.command.application.temp;

import atwoz.atwoz.admin.presentation.temp.dto.GrantMissionHeartRequest;
import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransaction;
import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransactionCommandRepository;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminTempService {

    private final MemberCommandRepository memberCommandRepository;
    private final HeartTransactionCommandRepository heartTransactionCommandRepository;

    @Transactional
    public void grantMissionHeart(GrantMissionHeartRequest request) {
        Member member   = memberCommandRepository.findById(request.memberId())
                .orElseThrow(MemberNotFoundException::new);
        HeartAmount heartAmount = HeartAmount.from(request.heartAmount());
        member.gainMissionHeart(heartAmount);
        HeartTransaction heartTransaction = HeartTransaction.of(member.getId(), TransactionType.MISSION, heartAmount, member.getHeartBalance());
        heartTransactionCommandRepository.save(heartTransaction);
    }
}
