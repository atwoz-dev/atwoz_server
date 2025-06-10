package atwoz.atwoz.member.command.application.sms;

import atwoz.atwoz.member.command.infra.sms.BizgoMessanger;
import org.junit.jupiter.api.Test;

public class TempTest {
    private BizgoMessanger bizgoMessanger = new BizgoMessanger();

    @Test
    public void test1() {
        bizgoMessanger.sendMessage("hi", "01079993047");
    }
}
