package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.admin.command.domain.hobby.HobbyCommandRepository;
import atwoz.atwoz.admin.command.domain.job.JobCommandRepository;
import atwoz.atwoz.admin.command.domain.job.JobNotFoundException;
import atwoz.atwoz.member.command.application.member.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.command.domain.member.exception.InvalidHobbyIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberCommandRepository memberCommandRepository;
    private final JobCommandRepository jobCommandRepository;
    private final HobbyCommandRepository hobbyCommandRepository;

    @Transactional
    public void updateMember(Long memberId, MemberProfileUpdateRequest request) {
        Member member = getMemberById(memberId);

        validateJobId(request.jobId());
        validateHobbyIds(request.hobbyIds());

        member.updateProfile(MemberMapper.toMemberProfile(request));
    }

    @Transactional
    public void changeToDormant(Long memberId) {
        getMemberById(memberId).changeToDormant();
    }

    private void validateJobId(Long jobId) {
        if (jobId != null && !jobCommandRepository.existsById(jobId)) {
            throw new JobNotFoundException();
        }
    }

    private void validateHobbyIds(Set<Long> hobbyIdList) {
        if (hobbyIdList != null && hobbyCommandRepository.countAllByIdIsIn(hobbyIdList) != hobbyIdList.size()) {
            throw new InvalidHobbyIdException();
        }
    }

    private Member getMemberById(Long memberId) {
        return memberCommandRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
