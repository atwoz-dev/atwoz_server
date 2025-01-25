package atwoz.atwoz.member.application;

import atwoz.atwoz.hobby.domain.HobbyRepository;
import atwoz.atwoz.job.domain.JobRepository;
import atwoz.atwoz.job.exception.JobNotFoundException;
import atwoz.atwoz.member.application.dto.MemberProfileResponse;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.application.exception.MemberNotFoundException;
import atwoz.atwoz.member.domain.member.Member;
import atwoz.atwoz.member.domain.member.MemberProfile;
import atwoz.atwoz.member.domain.member.MemberRepository;
import atwoz.atwoz.member.domain.member.exception.InvalidHobbyIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberRepository memberRepository;
    private final JobRepository jobRepository;
    private final HobbyRepository hobbyRepository;

    @Transactional
    public void updateMember(Long memberId, MemberProfileUpdateRequest request) {
        Member member = getMemberById(memberId);

        validateJobId(request.jobId());
        validateHobbyIds(request.hobbyIds());

        member.updateProfile(MemberMapper.toMemberProfile(request));
    }

    @Transactional(readOnly = true)
    public MemberProfileResponse getProfile(Long memberId) {
        MemberProfile memberProfile = getMemberById(memberId).getProfile();
        List<String> hobbyNames = getHobbyNames(memberProfile.getHobbyIds());
        String jobName = getJobName(memberProfile.getJobId());
        return MemberMapper.toMemberProfileResponse(memberProfile, hobbyNames, jobName);
    }

    @Transactional
    public void changeToDormant(Long memberId) {
        getMemberById(memberId).changeToDormant();
    }

    private void validateJobId(Long jobId) {
        if (jobId != null && !jobRepository.existsById(jobId)) {
            throw new JobNotFoundException();
        }
    }

    private void validateHobbyIds(Set<Long> hobbyIdList) {
        if (hobbyIdList != null && hobbyRepository.countAllByIdIsIn(hobbyIdList) != hobbyIdList.size()) {
            throw new InvalidHobbyIdException();
        }
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }

    private List<String> getHobbyNames(Set<Long> hobbyIds) {
        return hobbyRepository.findByIdIn(hobbyIds).stream()
                .map(hobby -> hobby.getName())
                .toList();
    }

    private String getJobName(Long jobId) {
        if (jobRepository.findById(jobId).isPresent()) {
            return jobRepository.findById(jobId).get().getName();
        }
        return null;
    }
}
