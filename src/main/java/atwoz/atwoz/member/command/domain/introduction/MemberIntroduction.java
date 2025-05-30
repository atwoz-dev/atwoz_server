package atwoz.atwoz.member.command.domain.introduction;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.member.command.domain.introduction.event.MemberIntroducedEvent;
import atwoz.atwoz.member.command.domain.introduction.exception.InvalidMemberIntroductionContentException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "member_introductions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberIntroduction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private Long memberId;

    @Getter
    private Long introducedMemberId;

    private MemberIntroduction(@NonNull Long memberId, @NonNull Long introducedMemberId) {
        this.memberId = memberId;
        this.introducedMemberId = introducedMemberId;
    }

    public static MemberIntroduction of(Long memberId, Long introducedMemberId, String content) {
        validateContent(content);
        MemberIntroduction memberIntroduction = new MemberIntroduction(memberId, introducedMemberId);
        Events.raise(MemberIntroducedEvent.of(memberId, content));
        return memberIntroduction;
    }

    private static void validateContent(@NonNull String content) {
        if (content.isBlank()) {
            throw new InvalidMemberIntroductionContentException("소개 내용은 비어있을 수 없습니다.");
        }
    }
}
