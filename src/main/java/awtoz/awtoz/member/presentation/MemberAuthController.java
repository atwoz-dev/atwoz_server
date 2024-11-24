package awtoz.awtoz.member.presentation;

import awtoz.awtoz.member.application.MemberAuthService;
import awtoz.awtoz.member.application.dto.MemberLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberAuthController {

    private final MemberAuthService memberAuthService;

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponse> login(@RequestBody String phoneNumber) {

        return new ResponseEntity<>(memberAuthService.login(phoneNumber), HttpStatus.OK);
    }
}
