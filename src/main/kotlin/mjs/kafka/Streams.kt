package mjs.kafka

import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.TimeWindows
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.time.Duration
import javax.annotation.PostConstruct

@Configuration
//@Enable
class Streams(
    val builder: StreamsBuilder
) {

    @Value("\${inputStream}")
    lateinit var inputStream: String

    @PostConstruct
    fun groupTransactionEvents() =
        builder.stream<String, Wrapper>(inputStream)
            .groupByKey()
            .windowedBy(TimeWindows.of(Duration.ofMinutes(60L)))
            .aggregate<String, OrgCustomer>(
                { OrgCustomer() },
                { key, value, customer ->
                    when(value.data is )
                }
            )


}