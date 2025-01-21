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
import atwoz.atwoz.member.exception.KakaoIdAlreadyExistsException;
import atwoz.atwoz.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.exception.PhoneNumberAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JobRepository jobRepository;
    private final HobbyRepository hobbyRepository;


    @Transactional
    public MemberProfileResponse updateMember(Long memberId, MemberProfileUpdateRequest request) {
        Member member = findById(memberId);

        validateJobId(request.jobId());
        validateHobbyIdList(request.hobbyIds());

        member.updateMemberProfile(MemberMapper.toMemberProfile(memberId, request));
        return MemberMapper.toMemberProfileResponse(member);
    }

    @Transactional
    public void transitionToDormant(Long memberId) {
        findById(memberId).transitionToDormant();
    }

    @Transactional
    public void updateKakaoId(Long memberId, String kakaoId) {
        if (existsAnotherMemberByKakaoId(kakaoId, memberId)) {
            throw new KakaoIdAlreadyExistsException();
        }
        Member member = findById(memberId);
        member.updateKaKaoId(KakaoId.from(kakaoId));
    }

    @Transactional
    public void updatePhoneNumber(Long memberId, String phoneNumber) {
        if (existsAnotherMemberByPhoneNumber(phoneNumber, memberId)) {
            throw new PhoneNumberAlreadyExistsException();
        }

        Member member = findById(memberId);
        member.updatePhoneNumber(phoneNumber);
    }

    public MemberProfileResponse getProfile(Long memberId) {
        Member member = findById(memberId);
        return MemberMapper.toMemberProfileResponse(member);
    }

    public MemberContactResponse getContactAll(Long memberId) {
        Member member = findById(memberId);
        return MemberMapper.toMemberContactResponse(member);
    }

    private Member findById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());
    }

    private boolean existsAnotherMemberByPhoneNumber(String phoneNumber, Long memberId) {
        Optional<Member> member = memberRepository.findByPhoneNumber(phoneNumber);

        if (member.isPresent() && member.get().getId() != memberId) {
            return true;
        }
        return false;
    }

    private boolean existsAnotherMemberByKakaoId(String kakaoId, Long memberId) {
        Optional<Member> member = memberRepository.findByKakaoId(kakaoId);

        if (member.isPresent() && member.get().getId() != memberId) {
            return true;
        }
        return false;
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
