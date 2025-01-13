package atwoz.atwoz.member;

import atwoz.atwoz.hobby.domain.HobbyRepository;
import atwoz.atwoz.job.domain.JobRepository;
import atwoz.atwoz.member.application.MemberService;
import atwoz.atwoz.member.domain.member.MemberRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberContactUpdateTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private HobbyRepository hobbyRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private MemberService memberService;

    /**
     * TODO : 테스트 코드 작성
     * 1. 휴대폰 번호 수정 케이스 (성공 / 실패)
     * 2. 카카오톡 아이디 설정 (성공 / 실패)
     */

}
