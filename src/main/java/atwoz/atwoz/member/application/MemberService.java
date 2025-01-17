package atwoz.atwoz.member.application;

import atwoz.atwoz.hobby.domain.HobbyRepository;
import atwoz.atwoz.job.domain.JobRepository;
import atwoz.atwoz.job.exception.JobNotFoundException;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.domain.member.Member;
import atwoz.atwoz.member.domain.member.MemberRepository;
import atwoz.atwoz.member.exception.InvalidHobbyIdException;
import atwoz.atwoz.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        validateHobbyIdList(request.hobbyIds());

        member.updateProfile(MemberMapper.toMemberProfile(memberId, request));
        return MemberMapper.toMemberProfileUpdateResponse(member);
    }

    private Member findById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());
    }

    private void validateJobId(Long jobId) {
        if (jobId != null && !jobRepository.existsById(jobId)) {
            throw new JobNotFoundException();
        }
    }

    private void validateHobbyIdList(List<Long> hobbyIdList) {
        if (hobbyIdList != null && hobbyRepository.countHobbiesByIdIn(hobbyIdList) != hobbyIdList.size()) {
            throw new InvalidHobbyIdException();
        }
    }
}
