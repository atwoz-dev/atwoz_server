package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberHobby extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    private Long hobbyId;

    public static MemberHobby of(Long memberId, Long hobbyId) {
        MemberHobby memberHobby = new MemberHobby();
        memberHobby.setMemberId(memberId);
        memberHobby.setHobbyId(hobbyId);

        return memberHobby;
    }

    private void setMemberId(@NonNull Long memberId) {
        this.memberId = memberId;
    }

    private void setHobbyId(@NonNull Long hobbyId) {
        this.hobbyId = hobbyId;
    }
}
