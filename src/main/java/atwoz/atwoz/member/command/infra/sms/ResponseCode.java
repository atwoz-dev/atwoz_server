package atwoz.atwoz.member.command.infra.sms;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS("A000"), EXFIRED_TOKEN("A001");

    private final String code;

    ResponseCode(String code) {
        this.code = code;
    }
}
