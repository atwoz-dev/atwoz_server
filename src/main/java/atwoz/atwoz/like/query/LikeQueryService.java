package atwoz.atwoz.like.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeQueryService {
    private final LikeQueryRepository likeQueryRepository;

    public List<LikeView> findSentLikes(long senderId, Long lastLikeId) {
        return likeQueryRepository.findSentLikes(senderId, lastLikeId).stream()
            .map(raw -> new LikeView(
                raw.likeId(),
                raw.opponentId(),
                raw.profileImageUrl(),
                raw.nickname(),
                raw.city(),
                LocalDate.now().getYear() - raw.yearOfBirth(),
                raw.isMutualLike(),
                raw.createdAt()
            ))
            .toList();
    }

    public List<LikeView> findReceivedLikes(long receiverId, Long lastLikeId) {
        return likeQueryRepository.findReceivedLikes(receiverId, lastLikeId).stream()
            .map(raw -> new LikeView(
                raw.likeId(),
                raw.opponentId(),
                raw.profileImageUrl(),
                raw.nickname(),
                raw.city(),
                LocalDate.now().getYear() - raw.yearOfBirth(),
                raw.isMutualLike(),
                raw.createdAt()
            ))
            .toList();
    }
}
