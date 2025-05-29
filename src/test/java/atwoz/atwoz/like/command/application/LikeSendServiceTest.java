package atwoz.atwoz.like.command.application;

import atwoz.atwoz.like.command.domain.Like;
import atwoz.atwoz.like.command.domain.LikeCommandRepository;
import atwoz.atwoz.like.presentation.LikeLevelRequest;
import atwoz.atwoz.like.presentation.LikeSendRequest;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeSendServiceTest {

    @Mock
    LikeCommandRepository likeCommandRepository;

    @Mock
    MemberCommandRepository memberCommandRepository;

    @InjectMocks
    LikeSendService likeSendService;

    @Test
    @DisplayName("좋아요를 성공적으로 보낸다.")
    void sendLikeSuccessfully() {
        // given
        var senderId = 1L;
        var receiverId = 2L;
        var likeLevel = LikeLevelRequest.INTERESTED;
        var request = new LikeSendRequest(receiverId, likeLevel);

        given(likeCommandRepository.existsBySenderIdAndReceiverId(senderId, receiverId)).willReturn(false);

        var mockNickname = mock(Nickname.class);
        given(mockNickname.getValue()).willReturn("nickname");

        var mockProfile = mock(MemberProfile.class);
        given(mockProfile.getNickname()).willReturn(mockNickname);

        var mockMember = mock(Member.class);
        given(mockMember.getProfile()).willReturn(mockProfile);

        given(memberCommandRepository.findById(senderId))
            .willReturn(Optional.of(mockMember));

        // when
        likeSendService.send(senderId, request);

        // then
        verify(likeCommandRepository).save(any(Like.class));
    }

    @Test
    @DisplayName("이미 좋아요를 보낸 경우 예외가 발생한다.")
    void throwExceptionWhenLikeAlreadyExists() {
        // given
        var senderId = 1L;
        var receiverId = 2L;
        var likeLevel = LikeLevelRequest.INTERESTED;
        var request = new LikeSendRequest(receiverId, likeLevel);

        given(likeCommandRepository.existsBySenderIdAndReceiverId(senderId, receiverId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> likeSendService.send(senderId, request))
            .isInstanceOf(LikeAlreadyExistsException.class);
    }
}