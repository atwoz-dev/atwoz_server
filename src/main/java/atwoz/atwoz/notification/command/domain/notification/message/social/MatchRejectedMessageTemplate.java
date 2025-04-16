package atwoz.atwoz.notification.command.domain.notification.message.social;

import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplate;
import atwoz.atwoz.notification.command.domain.notification.message.MessageTemplateParameters;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchRejectedMessageTemplate implements MessageTemplate {

    private final MemberCommandRepository memberCommandRepository;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.MATCH_REJECTED;
    }

    @Override
    public String getTitle(MessageTemplateParameters parameters) {
        Member receiver = memberCommandRepository.findById(parameters.getReceiverId())
                .orElseThrow(MemberNotFoundException::new);
        return receiver.getProfile().getNickname() + "님이 관심을 거절하셨습니다.";
    }

    @Override
    public String getContent(MessageTemplateParameters parameters) {
        return null;
    }
}
