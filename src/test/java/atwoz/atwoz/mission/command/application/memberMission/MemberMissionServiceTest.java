package atwoz.atwoz.mission.command.application.memberMission;

import atwoz.atwoz.member.command.domain.member.Gender;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.mission.command.application.memberMission.exception.MemberNotFoundException;
import atwoz.atwoz.mission.command.domain.memberMission.MemberMission;
import atwoz.atwoz.mission.command.domain.memberMission.MemberMissionCommandRepository;
import atwoz.atwoz.mission.command.domain.mission.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MemberMissionServiceTest {

    @Mock
    private MemberMissionCommandRepository memberMissionCommandRepository;

    @Mock
    private MissionCommandRepository missionCommandRepository;

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @InjectMocks
    private MemberMissionService memberMissionService;


    @Nested
    @DisplayName("미션 수행")
    class doMemberMission {
        Mission mission;
        Member member;
        MemberMission memberMission;

        long memberId = 1L;
        long missionId = 2L;

        @BeforeEach
        void setUp() {
            mission = Mission.create(ActionType.LIKE, FrequencyType.DAILY, TargetGender.MALE, 1, 2, 4, true);
            member = Member.fromPhoneNumber("01012345689");
            member.updateProfile(MemberProfile.builder().gender(Gender.MALE).build());
            memberMission = MemberMission.create(memberId, missionId);

            ReflectionTestUtils.setField(member, "id", memberId);
            ReflectionTestUtils.setField(mission, "id", missionId);
        }

        @Test
        @DisplayName("멤버가 존재하지 않는 경우, 예외를 발생합니다.")
        void throwExceptionWhenMemberIsNotFound() {
            // Given
            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.empty());

            // When & Then
            Assertions.assertThatThrownBy(
                    () -> memberMissionService.createOrUpdate(memberId, mission.getActionType().name()))
                .isInstanceOf(MemberNotFoundException.class);
        }


        @Test
        @DisplayName("멤버의 미션이 존재하지 않는 경우, 새롭게 생성합니다.")
        void createMemberMissionWhenNotExists() {
            // Given
            Mockito.when(missionCommandRepository.findByActionTypeAndTargetGender(mission.getActionType(),
                    mission.getTargetGender()))
                .thenReturn(List.of(mission));
            Mockito.when(memberCommandRepository.findById(memberId))
                .thenReturn(Optional.of(member));
            Mockito.when(memberMissionCommandRepository.findByMemberIdAndMissionIdOnToday(memberId, missionId))
                .thenReturn(Optional.empty());
            Mockito.when(memberMissionCommandRepository.save(Mockito.any()))
                .thenReturn(memberMission);

            // When
            memberMissionService.createOrUpdate(memberId, mission.getActionType().name());

            // Then
            Mockito.verify(memberMissionCommandRepository).save(Mockito.any(MemberMission.class));
            Assertions.assertThat(memberMission.getSuccessCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("멤버의 미션이 존재하는 경우, 해당 멤버-미션을 수행합니다.")
        void doMemberMissionWhenExists() {
            // Given
            Mockito.when(missionCommandRepository.findByActionTypeAndTargetGender(mission.getActionType(),
                    mission.getTargetGender()))
                .thenReturn(List.of(mission));
            Mockito.when(memberCommandRepository.findById(memberId))
                .thenReturn(Optional.of(member));
            Mockito.when(memberMissionCommandRepository.findByMemberIdAndMissionIdOnToday(memberId, missionId))
                .thenReturn(Optional.of(memberMission));

            // When
            memberMissionService.createOrUpdate(memberId, mission.getActionType().name());

            // Then
            Mockito.verify(memberMissionCommandRepository, Mockito.never()).save(Mockito.any(MemberMission.class));
            Assertions.assertThat(memberMission.getSuccessCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("멤버가 해당 미션을 완료한 경우, 해당 멤버-미션을 수행하지 않습니다.")
        void notDoMemberMissionWhenIsCompleted() {
            // Given
            MemberMission completedMemberMission = Mockito.mock(MemberMission.class);
            Mockito.when(missionCommandRepository.findByActionTypeAndTargetGender(mission.getActionType(),
                    mission.getTargetGender()))
                .thenReturn(List.of(mission));
            Mockito.when(memberCommandRepository.findById(memberId))
                .thenReturn(Optional.of(member));
            Mockito.when(memberMissionCommandRepository.findByMemberIdAndMissionIdOnToday(memberId, missionId))
                .thenReturn(Optional.of(completedMemberMission));
            Mockito.when(completedMemberMission.isCompleted()).thenReturn(true);

            // When
            memberMissionService.createOrUpdate(memberId, mission.getActionType().name());

            // Then
            Mockito.verify(memberMissionCommandRepository, Mockito.never()).save(Mockito.any(MemberMission.class));
            Mockito.verify(completedMemberMission, Mockito.never())
                .countPlus(mission.getRequiredAttempt(), mission.getRepeatableCount());
        }
    }
}
