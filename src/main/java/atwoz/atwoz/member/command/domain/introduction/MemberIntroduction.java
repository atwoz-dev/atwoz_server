package atwoz.atwoz.member.command.domain.introduction;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.member.command.domain.introduction.event.MemberIntroducedEvent;
import atwoz.atwoz.member.command.domain.introduction.exception.InvalidIntroductionMemberIdException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "member_introductions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberIntroduction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Long introducedMemberId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private IntroductionType type;

    private MemberIntroduction(@NonNull Long memberId, @NonNull Long introducedMemberId,
        @NonNull IntroductionType type) {
        validateMemberId(memberId, introducedMemberId);
        this.memberId = memberId;
        this.introducedMemberId = introducedMemberId;
        this.type = type;
    }

    public static MemberIntroduction of(Long memberId, Long introducedMemberId, @NonNull IntroductionType type) {
        MemberIntroduction memberIntroduction = new MemberIntroduction(memberId, introducedMemberId, type);
        Events.raise(MemberIntroducedEvent.of(memberId, type.getDescription(), type.name()));
        return memberIntroduction;
    }

    private void validateMemberId(@NonNull Long memberId, @NonNull Long introducedMemberId) {
        if (memberId.equals(introducedMemberId)) {
            throw new InvalidIntroductionMemberIdException(memberId, introducedMemberId);
        }
    }
}
