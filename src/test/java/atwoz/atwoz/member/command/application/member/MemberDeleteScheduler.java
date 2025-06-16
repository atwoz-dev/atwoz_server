package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberDeleteScheduler {
    @Mock
    private MemberCommandRepository memberCommandRepository;

    @InjectMocks
    private MemberDeleteScheduler memberDeleteScheduler;


    void test() {
        Mockito.doNothing().when(memberCommandRepository).deleteInIds(Mockito.any());
    }
}
