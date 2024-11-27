package awtoz.awtoz.member.presentation;

import awtoz.awtoz.common.auth.presentation.support.AuthMember;
import awtoz.awtoz.member.application.MemberAuthService;
import awtoz.awtoz.member.application.dto.MemberLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/auth")
public class MemberAuthController {

    private final MemberAuthService memberAuthService;

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponse> login(@RequestBody String phoneNumber) {
        return new ResponseEntity<>(memberAuthService.login(phoneNumber), HttpStatus.OK);
    }
}
