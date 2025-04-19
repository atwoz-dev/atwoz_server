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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberIdealServiceTest {

    @InjectMocks
    private MemberIdealService memberIdealService;

    @Mock
    private MemberIdealCommandRepository memberIdealCommandRepository;

    @Mock
    private HobbyCommandRepository hobbyCommandRepository;

    @Nested
    @DisplayName("update 메서드 테스트")
    class UpdateTest {
        private Set<Long> hobbyIds;
        private AgeRange ageRange;
        private Set<Region> regions;
        private Religion religion;
        private SmokingStatus smokingStatus;
        private DrinkingStatus drinkingStatus;
        private MemberIdealUpdateRequest request;

        @BeforeEach
        void setUp() {
            ageRange = AgeRange.of(20, 30);
            regions = Set.of(Region.SEOUL);
            religion = Religion.CHRISTIAN;
            smokingStatus = SmokingStatus.VAPE;
            drinkingStatus = DrinkingStatus.SOCIAL;
            hobbyIds = Set.of(1L, 2L);
            request = new MemberIdealUpdateRequest(
                    ageRange.getMinAge(),
                    ageRange.getMaxAge(),
                    regions.stream().map(Region::name).collect(Collectors.toSet()),
                    religion.name(),
                    smokingStatus.name(),
                    drinkingStatus.name(),
                    hobbyIds
            );
        }

        @Test
        @DisplayName("hobbyIds가 db에 존재하지 않는 경우 예외를 던진다.")
        void throwExceptionWhenHobbyIdsIsNotExists() {
            // given
            long memberId = 1L;
            when(hobbyCommandRepository.countAllByIdIsIn(hobbyIds)).thenReturn(1L);

            // when && then
            assertThatThrownBy(() -> memberIdealService.update(request, memberId))
                    .isInstanceOf(InvalidHobbyIdException.class);
        }

        @Test
        @DisplayName("memberIdeal이 존재하지 않는 경우 예외를 던진다.")
        void throwExceptionWhenMemberIdealIsNotExists() {
            // given
            long memberId = 1L;
            when(hobbyCommandRepository.countAllByIdIsIn(hobbyIds)).thenReturn(((long) hobbyIds.size()));
            when(memberIdealCommandRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

            // when && then
            assertThatThrownBy(() -> memberIdealService.update(request, memberId))
                    .isInstanceOf(MemberIdealNotFoundException.class);
        }

        @Test
        @DisplayName("memberIdeal이 존재하는 경우 memberIdeal을 업데이트한다.")
        void updateMemberIdeal() {
            long memberId = 1L;
            when(hobbyCommandRepository.countAllByIdIsIn(hobbyIds)).thenReturn(((long) hobbyIds.size()));
            MemberIdeal memberIdeal = mock(MemberIdeal.class);
            when(memberIdealCommandRepository.findByMemberId(memberId)).thenReturn(Optional.of(memberIdeal));

            // when
            memberIdealService.update(request, memberId);

            // then
            verify(memberIdeal).update(ageRange, hobbyIds, regions, religion, smokingStatus, drinkingStatus);
        }
    }

    @Nested
    @DisplayName("init 메서드 테스트")
    class InitTest {
        @Test
        @DisplayName("memberIdeal이 이미 존재하는 경우 예외를 던진다.")
        void throwExceptionWhenMemberIdealIsAlreadyExists() {
            // given
            long memberId = 1L;
            when(memberIdealCommandRepository.existsByMemberId(memberId)).thenReturn(true);

            // when && then
            assertThatThrownBy(() -> memberIdealService.init(memberId))
                    .isInstanceOf(MemberIdealAlreadyExistsException.class);
        }

        @Test
        @DisplayName("memberIdeal이 존재하지 않는 경우 memberIdeal을 생성한다.")
        void createMemberIdeal() {
            // given
            long memberId = 1L;
            when(memberIdealCommandRepository.existsByMemberId(memberId)).thenReturn(false);

            try(MockedStatic<MemberIdeal> memberIdealMock = mockStatic(MemberIdeal.class)) {
                MemberIdeal memberIdeal = mock(MemberIdeal.class);
                memberIdealMock.when(() -> MemberIdeal.init(memberId)).thenReturn(memberIdeal);

                // when
                memberIdealService.init(memberId);

                // then
                verify(memberIdealCommandRepository).save(memberIdeal);
            }
        }
    }
}