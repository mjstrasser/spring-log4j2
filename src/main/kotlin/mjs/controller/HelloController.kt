package mjs.controller

import org.apache.logging.log4j.kotlin.Logging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class HelloController : Logging {

    @GetMapping("/hello")
    fun sayHello(): String {
        logger.info("sayHello()")
        return "Hello there!!"
    }
}