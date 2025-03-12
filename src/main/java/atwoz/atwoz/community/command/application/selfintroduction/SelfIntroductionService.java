package atwoz.atwoz.community.command.application.selfintroduction;

import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroduction;
import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroductionCommandRepository;
import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionWriteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SelfIntroductionService {
    private final SelfIntroductionCommandRepository selfIntroductionCommandRepository;

    @Transactional
    public void write(SelfIntroductionWriteRequest request, Long memberId) {
        SelfIntroduction selfIntroduction = SelfIntroduction.write(memberId, request.title(), request.content());
        selfIntroductionCommandRepository.save(selfIntroduction);
    }
}
