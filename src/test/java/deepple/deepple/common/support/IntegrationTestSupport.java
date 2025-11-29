package deepple.deepple.common.support;

import deepple.deepple.common.config.RedisTestConfig;
import deepple.deepple.common.config.S3TestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({RedisTestConfig.class, S3TestConfig.class})
public abstract class IntegrationTestSupport {
}