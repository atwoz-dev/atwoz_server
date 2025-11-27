package atwoz.atwoz.member.command.application.member.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TemporarilySuspendedMemberException extends RuntimeException {
    private final LocalDateTime suspensionExpireAt;

    public TemporarilySuspendedMemberException(LocalDateTime suspensionExpireAt) {
        super("일시 정지된 유저입니다.");
        this.suspensionExpireAt = suspensionExpireAt;
    }
}
