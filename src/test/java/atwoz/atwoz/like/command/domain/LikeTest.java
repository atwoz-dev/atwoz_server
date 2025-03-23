package atwoz.atwoz.like.command.domain;

import atwoz.atwoz.like.command.domain.like.Like;
import atwoz.atwoz.like.command.domain.like.LikeLevel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LikeTest {
    @Nested
    @DisplayName("좋아요 생성 실패 케이스")
    class Fail {

        @Test
        @DisplayName("senderId가 null인 경우, 예외 발생")
        void throwExceptionWhenSenderIdIsNull() {
            // Given
            Long senderId = null;
            Long receiverId = 1L;
            LikeLevel likeLevel = LikeLevel.INTEREST;

            // When & Then
            Assertions.assertThatThrownBy(() -> Like.from(senderId, receiverId, likeLevel))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("receiverId가 null인 경우, 예외 발생")
        void throwExceptionWhenReceiverIdIsNull() {
            // Given
            Long senderId = 1L;
            Long receiverId = null;
            LikeLevel likeLevel = LikeLevel.INTEREST;

            // When & Then
            Assertions.assertThatThrownBy(() -> Like.from(senderId, receiverId, likeLevel))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("likeLevel이 null인 경우, 예외 발생")
        void throwExceptionWhenLikeLevelIsNull() {
            // Given
            Long senderId = 1L;
            Long receiverId = 2L;
            LikeLevel likeLevel = null;

            // When & Then
            Assertions.assertThatThrownBy(() -> Like.from(senderId, receiverId, likeLevel))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    @DisplayName("좋아요 생성")
    void createLike() {
        // Given
        Long senderId = 1L;
        Long receiverId = 2L;
        LikeLevel likeLevel = LikeLevel.INTEREST;

        // When
        Like like = Like.from(senderId, receiverId, likeLevel);

        // Then
        Assertions.assertThat(like.getSenderId()).isEqualTo(senderId);
        Assertions.assertThat(like.getReceiverId()).isEqualTo(receiverId);
        Assertions.assertThat(like.getLikeLevel()).isEqualTo(likeLevel);
    }
}
