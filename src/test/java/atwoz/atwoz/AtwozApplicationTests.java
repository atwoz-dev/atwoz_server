package atwoz.atwoz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(RedisTestConfig.class)
class AtwozApplicationTests {


    @Test
    void contextLoads() {

    }
}
