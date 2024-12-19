package atwoz.atwoz.member.domain;

import atwoz.atwoz.member.domain.member.MemberHobby;
import atwoz.atwoz.member.exception.InvalidHobbyIdException;
import atwoz.atwoz.member.exception.InvalidMemberIdException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemberHobbyTest {

    @Test
    @DisplayName("취미 ID가 null인 경우, 유효하지 않다.")
    public void invalidWhenHobbyIdIsNull() {
        // Given
        Long hobbyId = null;
        Long memberId = 1L;

        // When & Then
        Assertions.assertThatThrownBy(() -> MemberHobby.builder()
                        .hobbyId(hobbyId)
                        .memberId(memberId)
                        .build())
                .isInstanceOf(InvalidHobbyIdException.class);
    }

    @Test
    @DisplayName("멤버 ID가 null인 경우, 유효하지 않다.")
    public void invalidWhenMemberIdIsNull() {
        // Given
        Long hobbyId = 1L;
        Long memberId = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> MemberHobby.builder()
                        .hobbyId(hobbyId)
                        .memberId(memberId)
                        .build())
                .isInstanceOf(InvalidMemberIdException.class);
    }
}
