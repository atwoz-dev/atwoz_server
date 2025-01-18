package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "member_hobbies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberHobby extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    private Long hobbyId;

    public static MemberHobby of(Long memberId, Long hobbyId) {
        return new MemberHobby(memberId, hobbyId);
    }

    private MemberHobby(@NonNull Long memberId, @NonNull Long hobbyId) {
        this.memberId = memberId;
        this.hobbyId = hobbyId;
    }
}
