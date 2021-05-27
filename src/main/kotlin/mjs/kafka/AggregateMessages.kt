package mjs.kafka

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Grouped
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.kstream.Produced
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Value

//@Configuration
//@Enable
class AggregateMessages(
    private val builder: StreamsBuilder,
    @Value("\${inputTopic}") val inputTopic: String,
    @Value("\${outputTopic}") val outputTopic: String,
) : Logging {

    //    @PostConstruct
    fun aggregateIntoTransactions() {
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
            .filter { _, trans -> trans.isComplete }
            .toStream()
            .peek { k, v -> logger.debug { ">> Producing $k: $v" } }
            .to(
                outputTopic,
                Produced.with(Serdes.String(), serdeFor<Transaction>())
            )
    }

}