package deepple.deepple.like.command.application;

import deepple.deepple.block.application.required.BlockRepository;
import deepple.deepple.common.event.Events;
import deepple.deepple.like.command.application.exception.LikeReceiverInactiveException;
import deepple.deepple.like.command.application.exception.LikeSameGenderException;
import deepple.deepple.like.command.domain.Like;
import deepple.deepple.like.command.domain.LikeCommandRepository;
import deepple.deepple.like.command.domain.LikeSentEvent;
import deepple.deepple.like.presentation.LikeSendRequest;
import deepple.deepple.member.command.domain.member.Member;
import deepple.deepple.member.command.domain.member.MemberCommandRepository;
import deepple.deepple.mission.command.application.memberMission.MemberMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static deepple.deepple.like.command.application.LikeMapper.toLikeLevel;
import static deepple.deepple.mission.command.domain.mission.ActionType.LIKE;

@Service
@RequiredArgsConstructor
public class LikeSendService {

    private final MemberMissionService memberMissionService;
    private final LikeCommandRepository likeCommandRepository;
    private final MemberCommandRepository memberCommandRepository;
    private final BlockRepository blockRepository;

    @Transactional
    public boolean send(long senderId, LikeSendRequest request) {
        long receiverId = request.receiverId();

        if (likeCommandRepository.existsBySenderIdAndReceiverId(senderId, receiverId)) {
            throw new LikeAlreadyExistsException(senderId, receiverId);
        }

        var sender = memberCommandRepository.findById(senderId)
            .orElseThrow(() -> new MemberNotFoundException(senderId));
        var receiver = memberCommandRepository.findById(receiverId)
            .orElseThrow(() -> new MemberNotFoundException(receiverId));
        validateMember(sender, receiver);

        var like = Like.of(senderId, receiverId, toLikeLevel(request.likeLevel()));
        likeCommandRepository.save(like);

        boolean hasProcessedMission = memberMissionService.executeMissionsByAction(senderId, LIKE.name());
        Events.raise(LikeSentEvent.of(senderId, sender.getProfile().getNickname().getValue(), receiverId));

        return hasProcessedMission;
    }

    private void validateMember(Member sender, Member receiver) {
        if (!receiver.isActive()) {
            throw new LikeReceiverInactiveException(receiver.getId());
        }
        if (sender.hasSameGender(receiver)) {
            throw new LikeSameGenderException(sender.getId(), receiver.getId());
        }
        if (blockRepository.existsByBlockerIdAndBlockedId(sender.getId(), receiver.getId())) {
            throw new LikeBlockedException(sender.getId(), receiver.getId());
        }
        if (blockRepository.existsByBlockerIdAndBlockedId(receiver.getId(), sender.getId())) {
            throw new LikeBlockedException(sender.getId(), receiver.getId());
        }
    }
}
