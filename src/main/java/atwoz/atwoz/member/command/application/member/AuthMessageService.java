package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.member.command.infra.member.AuthMessageRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthMessageService {
    private final AuthMessageRedisRepository authMessageRedisRepository;

    public void sendMessage(String message) {

    }

    public void authenticate(String phoneNumber, String code) {

    }
}
