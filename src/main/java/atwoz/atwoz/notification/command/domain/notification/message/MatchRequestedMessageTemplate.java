package atwoz.atwoz.notification.command.domain.notification.message;

import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchRequestedMessageTemplate implements MessageTemplate {

    private final MemberCommandRepository memberCommandRepository;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.MATCH_REQUESTED;
    }

    @Override
    public String getTitle(MessageTemplateParameters parameters) {
        Member sender = memberCommandRepository.findById(parameters.getSenderId()).orElseThrow(MemberNotFoundException::new);
        return sender.getProfile().getNickname() + "님께 매치가 요청되었습니다.";
    }

    @Override
    public String getContent(MessageTemplateParameters parameters) {
        return null;
    }
}
