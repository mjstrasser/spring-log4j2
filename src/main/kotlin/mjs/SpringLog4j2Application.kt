package mjs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringLog4j2Application

fun main(args: Array<String>) {
    runApplication<SpringLog4j2Application>(*args)
}
