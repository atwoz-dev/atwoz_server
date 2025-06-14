package atwoz.atwoz.member.command.infra.member.sms.dto;

public record BizgoMessageRequest(
    String from,
    String to,
    String text
) {
}
