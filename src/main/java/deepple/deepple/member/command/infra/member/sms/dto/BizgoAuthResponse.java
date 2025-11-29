package deepple.deepple.member.command.infra.member.sms.dto;

public record BizgoAuthResponse(
    String code,
    String result,
    DataField data

) {
    public record DataField(
        String schema,
        String expired,
        String token
    ) {}
}
