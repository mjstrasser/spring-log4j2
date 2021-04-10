package mjs

import org.apache.logging.log4j.kotlin.Logging
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringLog4j2ApplicationTests : Logging {

    @Test
    fun contextLoads() {
        logger.info { "Running the test" }
    }

}
