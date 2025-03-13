package atwoz.atwoz.member.command.domain.introduction;

import atwoz.atwoz.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "member_introductions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberIntroduction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Long introducedMemberId;

    public static MemberIntroduction of(Long memberId, Long introducedMemberId) {
        return new MemberIntroduction(memberId, introducedMemberId);
    }

    private MemberIntroduction(@NonNull Long memberId, @NonNull Long introducedMemberId) {
        this.memberId = memberId;
        this.introducedMemberId = introducedMemberId;
    }
}
