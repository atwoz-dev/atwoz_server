package atwoz.atwoz.member.application;

import atwoz.atwoz.hobby.domain.HobbyRepository;
import atwoz.atwoz.job.domain.JobRepository;
import atwoz.atwoz.job.exception.JobNotFoundException;
import atwoz.atwoz.member.application.dto.MemberContactResponse;
import atwoz.atwoz.member.application.dto.MemberProfileResponse;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.domain.member.Member;
import atwoz.atwoz.member.domain.member.MemberRepository;
import atwoz.atwoz.member.domain.member.vo.KakaoId;
import atwoz.atwoz.member.exception.InvalidHobbyIdException;
import atwoz.atwoz.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.exception.PhoneNumberAlreadyExistsException;
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

        member.updateMemberProfile(MemberMapper.toMemberProfile(memberId, request));
        return MemberMapper.toMemberProfileUpdateResponse(member);
    }

    @Transactional
    public void transitionToDormant(Long memberId) {
        findById(memberId).transitionToDormant();
    }

    @Transactional
    public void updateKakaoId(Long memberId, String kakaoId) {
        findById(memberId).updateKaKaoId(KakaoId.from(kakaoId));
    }

    @Transactional
    public void updatePhoneNumber(Long memberId, String phoneNumber) {
        if (existsByPhoneNumber(phoneNumber)) {
            throw new PhoneNumberAlreadyExistsException();
        }

        Member member = findById(memberId);
        member.updatePhoneNumber(phoneNumber);
    }

    public MemberProfileResponse getProfile(Long memberId) {
        Member member = findById(memberId);
        return MemberMapper.toMemberProfileResponse(member);
    }

    public MemberContactResponse getContact(Long memberId) {
        Member member = findById(memberId);
        return MemberMapper.toMemberContactResponse(member);
    }

    private Member findById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());
    }

    private boolean existsByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber).isPresent();
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
