package atwoz.atwoz.community.command.application.selfintroduction;

import atwoz.atwoz.community.command.application.selfintroduction.exception.NotSelfIntroductionAuthorException;
import atwoz.atwoz.community.command.application.selfintroduction.exception.SelfIntroductionNotFoundException;
import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroduction;
import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroductionCommandRepository;
import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionWriteRequest;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SelfIntroductionService {
    private final SelfIntroductionCommandRepository selfIntroductionCommandRepository;
    private final MemberCommandRepository memberCommandRepository;

    @Transactional
    public void write(SelfIntroductionWriteRequest request, Long memberId) {
        validateMemberId(memberId);
        SelfIntroduction selfIntroduction = SelfIntroduction.write(memberId, request.title(), request.content());
        selfIntroductionCommandRepository.save(selfIntroduction);
    }

    @Transactional
    public void update(SelfIntroductionWriteRequest request, Long memberId, Long id) {
        validateMemberId(memberId);
        SelfIntroduction selfIntroduction = getSelfIntroductionById(id);
        validateSelfIntroductionAuthor(selfIntroduction.getMemberId(), memberId);

        selfIntroduction.update(request.title(), request.content());
    }

    @Transactional
    public void delete(Long id, Long memberId) {
        validateMemberId(memberId);
        validateSelfIntroductionAuthor(getSelfIntroductionById(id).getMemberId(), memberId);
        selfIntroductionCommandRepository.deleteById(id);
    }

    private void validateSelfIntroductionAuthor(Long memberIdFromSelfIntroduction, Long memberId) {
        if (memberIdFromSelfIntroduction != memberId) {
            throw new NotSelfIntroductionAuthorException();
        }
    }

    private SelfIntroduction getSelfIntroductionById(Long id) {
        return selfIntroductionCommandRepository.findById(id).orElseThrow(SelfIntroductionNotFoundException::new);
    }

    private void validateMemberId(Long memberId) {
        memberCommandRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
