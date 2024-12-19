package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.common.domain.BaseEntity;
import atwoz.atwoz.member.exception.InvalidHobbyIdException;
import atwoz.atwoz.member.exception.InvalidMemberIdException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberHobby extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    private Long hobbyId;

    @Builder
    private MemberHobby(Long memberId, Long hobbyId) {
        validateMemberId(memberId);
        validateHobbyId(hobbyId);
        this.memberId = memberId;
        this.hobbyId = hobbyId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getHobbyId() {
        return hobbyId;
    }

    private static void validateMemberId(Long memberId) {
        if (memberId == null) {
            throw new InvalidMemberIdException();
        }
    }

    private static void validateHobbyId(Long hobbyId) {
        if (hobbyId == null) {
            throw new InvalidHobbyIdException();
        }
    }
}
