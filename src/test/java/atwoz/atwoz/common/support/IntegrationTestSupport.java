package atwoz.atwoz.common.support;

import atwoz.atwoz.common.config.RedisTestConfig;
import atwoz.atwoz.common.config.S3TestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({RedisTestConfig.class, S3TestConfig.class})
public abstract class IntegrationTestSupport {
}