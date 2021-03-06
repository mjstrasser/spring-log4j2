package mjs.kafka

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.logging.log4j.kotlin.logger
import java.util.Properties

class AggregateMessagesTest : DescribeSpec({

    describe("Aggregate messages into transactions") {
        it("groups a transaction with a single (complete) event") {
            val builder = StreamsBuilder()

            AggregateMessages(builder, "events", "transactions")
                .aggregateIntoTransactions()

            val driver = testDriver(builder)
            val messagesTopic = driver.createInputTopic(
                "events",
                Serdes.String().serializer(),
                serializerFor<Message>(),
            )
            val transactionsTopic = driver.createOutputTopic(
                "transactions",
                Serdes.String().deserializer(),
                deserializerFor<Transaction>(),
            )

            val transactionId = randomTxnId()
            val crn = randomCrn()
            val message = Message(Header(transactionId, 1, true), Customer(crn, "O"))

            messagesTopic.pipeInput(message)

            with(transactionsTopic.readValue()) {
                logger().info { "Read value: $this" }
                id shouldBe transactionId
                messages shouldContain message
                isComplete shouldBe true
            }
        }
        it("groups a transaction with multiple events in any order") {
            val builder = StreamsBuilder()

            AggregateMessages(builder, "events", "transactions")
                .aggregateIntoTransactions()

            val driver = testDriver(builder)
            val messagesTopic = driver.createInputTopic(
                "events",
                Serdes.String().serializer(),
                serializerFor<Message>(),
            )
            val transactionsTopic = driver.createOutputTopic(
                "transactions",
                Serdes.String().deserializer(),
                deserializerFor<Transaction>(),
            )

            val transactionId = randomTxnId()
            val crn = randomCrn()
            val message1 = Message(Header(transactionId, 1, false), Customer(crn, "O"))
            val message2 = Message(Header(transactionId, 2, false), Organisation(crn, randomAcn()))
            val message3 = Message(Header(transactionId, 3, true), OrganisationName(crn, "2021-05-26", "Rider P/L"))

            messagesTopic.pipeKeyValueList(
                listOf(
                    KeyValue(randomKey(), message1),
                    KeyValue(randomKey(), message2),
                    KeyValue(randomKey(), message3),
                ).shuffled()
            )

            with(transactionsTopic.readValue()) {
                logger().info { "Read value: $this" }
                id shouldBe transactionId
                messages shouldContain message1
                messages shouldContain message2
                messages shouldContain message3
                isComplete shouldBe true
            }
        }
    }

})
