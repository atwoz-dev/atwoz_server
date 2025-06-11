package atwoz.atwoz.member.command.application.member.sms;

import atwoz.atwoz.member.command.application.member.exception.CodeNotMatchException;
import atwoz.atwoz.member.command.infra.member.AuthMessageRedisRepository;
import atwoz.atwoz.member.command.infra.member.sms.BizgoMessanger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthMessageService {
    private static final Random generator = new Random();
    private final AuthMessageRedisRepository authMessageRedisRepository;
    private final BizgoMessanger bizgoMessanger;

    public void sendMessage(String phoneNumber) {
        bizgoMessanger.sendMessage(getMessage(), phoneNumber);
    }

    public void authenticate(String phoneNumber, String code) {
        String value = authMessageRedisRepository.getByKey(phoneNumber);
        validateWithCode(value, code);
        authMessageRedisRepository.delete(phoneNumber);
    }

    private String getMessage() {
        String randomNumber = generateNumber();
        return "[에이투지] + 인증번호 [" + randomNumber + "]를 입력해주세요.";
    }

    private String generateNumber() {
        generator.setSeed(System.currentTimeMillis());
        return (generator.nextInt(100000) % 1000000) + "";
    }

    private void validateWithCode(String value, String code) {
        if (value == null || !value.equals(code)) {
            throw new CodeNotMatchException();
        }
    }
}
