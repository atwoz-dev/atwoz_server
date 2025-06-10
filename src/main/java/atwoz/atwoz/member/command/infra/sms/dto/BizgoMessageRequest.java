package atwoz.atwoz.member.command.infra.sms.dto;

public record BizgoMessageRequest(
    String from,
    String to,
    String text
) {
}
