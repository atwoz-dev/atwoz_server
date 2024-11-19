package awtoz.awtoz.member.auth;

import awtoz.awtoz.member.application.auth.MemberAuthService;
import awtoz.awtoz.member.application.auth.dto.MemberLoginResponse;
import awtoz.awtoz.member.domain.member.Member;
import awtoz.awtoz.member.domain.member.MemberRepository;
import awtoz.awtoz.member.infra.auth.MemberJwtTokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LoginTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberJwtTokenProvider memberJwtTokenProvider;

    @InjectMocks
    private MemberAuthService memberAuthService;

    @BeforeEach
    void setUp() {
        memberJwtTokenProvider = new MemberJwtTokenProvider();

        // 테스트용 환경 설정
        ReflectionTestUtils.setField(memberJwtTokenProvider, "secret", "this-is-secret-key-value-at-least-128-bytes");
        ReflectionTestUtils.setField(memberJwtTokenProvider, "accessTokenExpirationTime", 60 * 60 * 24);

        // @PostConstruct 대체 호출
        memberJwtTokenProvider.init();
        memberAuthService = new MemberAuthService(memberRepository, memberJwtTokenProvider);
    }

    @Test
    @DisplayName("DB에 없는 유저가 회원가입 시도")
    void testLoginNewMember() {
        String phoneNumber = "01012345678";
        Member mockMember = Member.createWithPhoneNumber("01012345678");


        Mockito.when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.ofNullable(null));
        Mockito.when(memberRepository.save(Mockito.any(Member.class))).thenReturn(mockMember);

        // When
        MemberLoginResponse response  = memberAuthService.login(phoneNumber);


        // Then
        Assertions.assertThat(response.accessToken()).isNotNull();
        Assertions.assertThat(response.isNeedProfile()).isTrue();
        Assertions.assertThat(response.isSuspended()).isFalse();
    }
}
