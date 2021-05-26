package mjs.kafka

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Grouped
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Value

//@Configuration
//@Enable
class Streams(
    private val builder: StreamsBuilder
) : Logging {

    @Value("\${inputTopic}")
    lateinit var inputTopic: String

    @Value("\${outputTopic}")
    lateinit var outputTopic: String

    //    @PostConstruct
    fun groupTransactionEvents() =
        builder.stream(
            inputTopic,
            Consumed.with(Serdes.String(), serdeFor<Message>())
        )
            .peek { k, v -> logger.debug { "<< Consumed $k: $v" } }
            .groupBy(
                { _, message -> message.header.transactionId },
                Grouped.with(Serdes.String(), serdeFor<Message>()),
            )
            .aggregate(
                { Transaction() },
                { _, message, trans -> trans.addMessage(message) },
                Materialized.with(Serdes.String(), serdeFor<Transaction>())
            )
            .toStream()
            .peek { k, v -> logger.debug { ">> Producing $k: $v" } }
            .to(
                outputTopic,
                Produced.with(Serdes.String(), serdeFor<Transaction>())
            )

}