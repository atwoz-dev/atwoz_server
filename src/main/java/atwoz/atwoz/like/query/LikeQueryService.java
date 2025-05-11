package atwoz.atwoz.like.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeQueryService {
    private static final int CLIENT_PAGE_SIZE = 12;
    private final LikeQueryRepository likeQueryRepository;

    public List<LikeView> findSentLikes(long senderId, Long lastLikeId) {
        List<RawLikeView> likes = likeQueryRepository.findSentLikes(senderId, lastLikeId);
        boolean hasMore = likes.size() > CLIENT_PAGE_SIZE;

        return likes.stream()
            .limit(CLIENT_PAGE_SIZE)
            .map(raw -> toLikeView(raw, hasMore))
            .toList();
    }

    public List<LikeView> findReceivedLikes(long receiverId, Long lastLikeId) {
        List<RawLikeView> likes = likeQueryRepository.findReceivedLikes(receiverId, lastLikeId);
        boolean hasMore = likes.size() > CLIENT_PAGE_SIZE;

        return likes.stream()
            .limit(CLIENT_PAGE_SIZE)
            .map(raw -> toLikeView(raw, hasMore))
            .toList();
    }

    private LikeView toLikeView(RawLikeView raw, boolean hasMore) {
        return new LikeView(
            raw.likeId(),
            raw.opponentId(),
            raw.profileImageUrl(),
            raw.nickname(),
            raw.city(),
            LocalDate.now().getYear() - raw.yearOfBirth(),
            raw.isMutualLike(),
            raw.createdAt(),
            hasMore
        );
    }
}