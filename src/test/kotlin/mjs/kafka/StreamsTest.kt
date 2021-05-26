package mjs.kafka

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TopologyTestDriver
import java.util.Properties

class StreamsTest : DescribeSpec({

    describe("Group transaction events") {
        it("groups a single event") {
            val builder = StreamsBuilder()
            val streams = Streams(builder)
            streams.inputTopic = "events"
            streams.outputTopic = "transactions"
            streams.groupTransactionEvents()

            val topology = builder.build()
//            logger().info(topology.describe())
            val driver = TopologyTestDriver(topology, Properties())
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
            val message = Message(Header(transactionId, 1, true), Customer(randomCrn(), "O"))

            val key = randomKey()
            messagesTopic.pipeInput(key, message)
            with(transactionsTopic.readValue()) {
                id shouldBe transactionId
                messages shouldContain message
                isComplete shouldBe true
            }
        }
    }

})
