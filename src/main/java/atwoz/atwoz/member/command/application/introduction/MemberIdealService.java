package atwoz.atwoz.member.command.application.introduction;

import atwoz.atwoz.member.command.application.introduction.exception.MemberIdealAlreadyExistsException;
import atwoz.atwoz.member.command.application.introduction.exception.MemberIdealNotFoundException;
import atwoz.atwoz.member.command.domain.introduction.MemberIdeal;
import atwoz.atwoz.member.command.domain.introduction.MemberIdealCommandRepository;
import atwoz.atwoz.member.command.domain.introduction.vo.AgeRange;
import atwoz.atwoz.member.command.domain.member.*;
import atwoz.atwoz.member.presentation.introduction.dto.MemberIdealUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberIdealService {
    private final MemberIdealCommandRepository memberIdealCommandRepository;

    /**
     * Initializes a MemberIdeal entity for the specified member.
     *
     * @param memberId the ID of the member for whom to initialize the ideal preferences
     * @throws MemberIdealAlreadyExistsException if a MemberIdeal already exists for the given member ID
     */
    @Transactional
    public void init(long memberId) {
        if (memberIdealCommandRepository.existsByMemberId(memberId)) {
            throw new MemberIdealAlreadyExistsException(memberId);
        }
        MemberIdeal memberIdeal = MemberIdeal.init(memberId);
        memberIdealCommandRepository.save(memberIdeal);
    }

    /**
     * Updates the introduction preferences for a member with the provided values.
     *
     * @param request the update request containing new preference values
     * @param memberId the ID of the member whose preferences are being updated
     * @throws MemberIdealNotFoundException if no introduction preferences exist for the given member
     */
    @Transactional
    public void update(MemberIdealUpdateRequest request, long memberId) {
        MemberIdeal memberIdeal = getMemberIdealByMemberId(memberId);
        AgeRange ageRange = AgeRange.of(request.minAge(), request.maxAge());
        Set<Hobby> hobbies = request.hobbies().stream().map(Hobby::from).collect(Collectors.toSet());
        Set<City> cities = request.cities().stream().map(City::from).collect(Collectors.toSet());
        Religion religion = Religion.from(request.religion());
        SmokingStatus smokingStatus = SmokingStatus.from(request.smokingStatus());
        DrinkingStatus drinkingStatus = DrinkingStatus.from(request.drinkingStatus());
        memberIdeal.update(ageRange, hobbies, cities, religion, smokingStatus, drinkingStatus);
    }

    /**
     * Retrieves the MemberIdeal entity for the specified member ID.
     *
     * @param memberId the ID of the member whose ideal preferences are to be fetched
     * @return the MemberIdeal associated with the given member ID
     * @throws MemberIdealNotFoundException if no MemberIdeal exists for the specified member
     */
    private MemberIdeal getMemberIdealByMemberId(long memberId) {
        return memberIdealCommandRepository.findByMemberId(memberId).orElseThrow(MemberIdealNotFoundException::new);
    }
}
