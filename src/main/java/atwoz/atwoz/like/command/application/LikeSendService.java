package atwoz.atwoz.like.command.application;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.like.command.domain.Like;
import atwoz.atwoz.like.command.domain.LikeCommandRepository;
import atwoz.atwoz.like.command.domain.LikeSentEvent;
import atwoz.atwoz.like.presentation.LikeSendRequest;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.mission.command.application.memberMission.MemberMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static atwoz.atwoz.like.command.application.LikeMapper.toLikeLevel;
import static atwoz.atwoz.mission.command.domain.mission.ActionType.LIKE;

@Service
@RequiredArgsConstructor
public class LikeSendService {

    private final MemberMissionService memberMissionService;
    private final LikeCommandRepository likeCommandRepository;
    private final MemberCommandRepository memberCommandRepository;

    @Transactional
    public boolean send(long senderId, LikeSendRequest request) {
        long receiverId = request.receiverId();

        if (likeCommandRepository.existsBySenderIdAndReceiverId(senderId, receiverId)) {
            throw new LikeAlreadyExistsException(senderId, receiverId);
        }

        var member = memberCommandRepository.findById(senderId)
            .orElseThrow(() -> new MemberNotFoundException(senderId));

        var like = Like.of(senderId, receiverId, toLikeLevel(request.likeLevel()));
        likeCommandRepository.save(like);

        boolean hasProcessedMission = memberMissionService.executeMissionsByAction(senderId, LIKE.name());
        Events.raise(LikeSentEvent.of(senderId, member.getProfile().getNickname().getValue(), receiverId));

        return hasProcessedMission;
    }
}
