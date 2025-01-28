package atwoz.atwoz.member.application;

import atwoz.atwoz.hobby.command.domain.HobbyCommandRepository;
import atwoz.atwoz.job.command.domain.JobCommandRepository;
import atwoz.atwoz.job.command.exception.JobNotFoundException;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.application.exception.MemberNotFoundException;
import atwoz.atwoz.member.domain.member.Member;
import atwoz.atwoz.member.domain.member.MemberRepository;
import atwoz.atwoz.member.domain.member.exception.InvalidHobbyIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JobCommandRepository jobCommandRepository;
    private final HobbyCommandRepository hobbyCommandRepository;

    @Transactional
    public MemberProfileUpdateResponse updateMember(Long memberId, MemberProfileUpdateRequest request) {
        Member member = findById(memberId);

        validateJobId(request.jobId());
        validateHobbyIds(request.hobbyIds());

        member.updateProfile(MemberMapper.toMemberProfile(request));
        return MemberMapper.toMemberProfileUpdateResponse(member);
    }

    private Member findById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }

    private void validateJobId(Long jobId) {
        if (jobId != null && !jobCommandRepository.existsById(jobId)) {
            throw new JobNotFoundException();
        }
    }

    private void validateHobbyIds(Set<Long> hobbyIdList) {
        if (hobbyIdList != null && hobbyCommandRepository.countHobbiesByIdIn(hobbyIdList) != hobbyIdList.size()) {
            throw new InvalidHobbyIdException();
        }
    }
}
