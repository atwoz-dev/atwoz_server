package atwoz.atwoz.like.command.application;

import atwoz.atwoz.like.command.domain.Like;
import atwoz.atwoz.like.command.domain.LikeCommandRepository;
import atwoz.atwoz.like.presentation.LikeSendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static atwoz.atwoz.like.command.application.LikeMapper.toLikeLevel;

@Service
@RequiredArgsConstructor
public class LikeSendService {
    private final LikeCommandRepository likeCommandRepository;

    @Transactional
    public void send(final long senderId, final LikeSendRequest request) {
        final long receiverId = request.receiverId();

        if (likeCommandRepository.existsBySenderIdAndReceiverId(senderId, receiverId)) {
            throw new LikeAlreadyExistsException(senderId, receiverId);
        }

        var like = Like.of(senderId, receiverId, toLikeLevel(request.likeLevel()));
        likeCommandRepository.save(like);
    }
}
