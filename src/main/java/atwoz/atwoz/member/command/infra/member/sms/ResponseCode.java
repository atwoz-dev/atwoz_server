package atwoz.atwoz.member.command.infra.member.sms;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS("A000"), EXPIRED_TOKEN("A001");

    private final String code;

    ResponseCode(String code) {
        this.code = code;
    }
}
