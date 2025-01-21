package atwoz.atwoz.member.command.application;

import atwoz.atwoz.hobby.domain.HobbyRepository;
import atwoz.atwoz.job.domain.JobRepository;
import atwoz.atwoz.job.exception.JobNotFoundException;
import atwoz.atwoz.member.command.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.command.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.command.application.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberRepository;
import atwoz.atwoz.member.command.domain.member.exception.InvalidHobbyIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JobRepository jobRepository;
    private final HobbyRepository hobbyRepository;

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
        if (jobId != null && !jobRepository.existsById(jobId)) {
            throw new JobNotFoundException();
        }
    }

    private void validateHobbyIds(Set<Long> hobbyIdList) {
        if (hobbyIdList != null && hobbyRepository.countHobbiesByIdIn(hobbyIdList) != hobbyIdList.size()) {
            throw new InvalidHobbyIdException();
        }
    }
}
