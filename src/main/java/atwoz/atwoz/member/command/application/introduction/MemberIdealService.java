package atwoz.atwoz.member.command.application.introduction;

import atwoz.atwoz.admin.command.domain.hobby.HobbyCommandRepository;
import atwoz.atwoz.member.command.application.introduction.exception.MemberIdealAlreadyExistsException;
import atwoz.atwoz.member.command.application.introduction.exception.MemberIdealNotFoundException;
import atwoz.atwoz.member.command.domain.introduction.MemberIdeal;
import atwoz.atwoz.member.command.domain.introduction.MemberIdealCommandRepository;
import atwoz.atwoz.member.command.domain.introduction.vo.AgeRange;
import atwoz.atwoz.member.command.domain.member.DrinkingStatus;
import atwoz.atwoz.member.command.domain.member.Region;
import atwoz.atwoz.member.command.domain.member.Religion;
import atwoz.atwoz.member.command.domain.member.SmokingStatus;
import atwoz.atwoz.member.command.domain.member.exception.InvalidHobbyIdException;
import atwoz.atwoz.member.presentation.introduction.dto.MemberIdealUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberIdealService {
    private final MemberIdealCommandRepository memberIdealCommandRepository;
    private final HobbyCommandRepository hobbyCommandRepository;

    @Transactional
    public void init(long memberId) {
        if (memberIdealCommandRepository.existsByMemberId(memberId)) {
            throw new MemberIdealAlreadyExistsException(memberId);
        }
        MemberIdeal memberIdeal = MemberIdeal.from(memberId);
        memberIdealCommandRepository.save(memberIdeal);
    }

    @Transactional
    public void update(MemberIdealUpdateRequest request, long memberId) {
        validateHobbyIds(request.hobbyIds());
        MemberIdeal memberIdeal = getMemberIdealByMemberId(memberId);
        AgeRange ageRange = AgeRange.of(request.minAge(), request.maxAge());
        Region region = Region.from(request.region());
        Religion religion = Religion.from(request.religion());
        SmokingStatus smokingStatus = SmokingStatus.from(request.smokingStatus());
        DrinkingStatus drinkingStatus = DrinkingStatus.from(request.drinkingStatus());
        memberIdeal.update(ageRange, request.hobbyIds(), region, religion, smokingStatus, drinkingStatus);
    }

    private MemberIdeal getMemberIdealByMemberId(long memberId) {
        return memberIdealCommandRepository.findByMemberId(memberId).orElseThrow(MemberIdealNotFoundException::new);
    }

    private void validateHobbyIds(Set<Long> hobbyIds) {
        if (hobbyIds == null || hobbyIds.isEmpty()) {
            return;
        }
        if (hobbyCommandRepository.countAllByIdIsIn(hobbyIds) != hobbyIds.size()) {
            throw new InvalidHobbyIdException();
        }
    }
}
