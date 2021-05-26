package mjs.kafka

import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import org.springframework.beans.factory.annotation.Value
import javax.annotation.PostConstruct

//@Configuration
//@Enable
class Streams(
    val builder: StreamsBuilder
) {

    @Value("\${inputStream}")
    lateinit var inputStream: String

    @PostConstruct
    fun groupTransactionEvents(): KTable<String, Transaction> = builder.stream<String, Message>(inputStream)
        .groupBy { _, value -> value.header.transactionId }
        .aggregate(
            { Transaction() },
            { _, message, trans -> trans.addMessage(message) },
        )

}