package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberHobby extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    private Long hobbyId;

    @Builder
    private MemberHobby(Long memberId, Long hobbyId) {
        setMemberId(memberId);
        setHobbyId(hobbyId);
    }

    private void setMemberId(@NonNull Long memberId) {
        this.memberId = memberId;
    }

    private void setHobbyId(@NonNull Long hobbyId) {
        this.hobbyId = hobbyId;
    }
}
