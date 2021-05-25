package mjs.kafka

import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.TimeWindows
import org.apache.kafka.streams.kstream.Windowed
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.time.Duration
import javax.annotation.PostConstruct

//@Configuration
//@Enable
class Streams(
    val builder: StreamsBuilder
) {

    @Value("\${inputStream}")
    lateinit var inputStream: String

    @PostConstruct
    fun groupTransactionEvents(): KTable<Windowed<String>, OrgCustomer> = builder.stream<String, Wrapper>(inputStream)
        .groupByKey()
        .windowedBy(TimeWindows.of(Duration.ofMinutes(60L)))
        .aggregate(
            { OrgCustomer() },
            { _, value, customer ->
                when (value.data) {
                    is Customer -> {
                        customer.id = value.data.id
                    }
                    is Organisation -> {
                        customer.id = value.data.customerId
                        customer.acn = value.data.acn
                    }
                    is OrganisationName -> {
                        customer.id = value.data.customerId
                        customer.name = value.data.name
                    }
                    else -> throw IllegalStateException("Aggregator got OrgCustomer type")
                }
                customer
            },
        )

}