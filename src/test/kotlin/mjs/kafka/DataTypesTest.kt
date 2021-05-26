package mjs.kafka

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataTypesTest : DescribeSpec({

    describe("Wrapped customer message") {
        val message = Message(
            header = Header(transactionId = "123123123", transactionEventCounter = 1),
            data = Customer(id = "123456789", customerType = "O"),
        )
        val json =
            """{"header":{"transactionId":"123123123","transactionEventCounter":1},"data":{"type":"mjs.kafka.Customer","id":"123456789","customerType":"O"}}"""
        it("serialises correctly") {
            Json.encodeToString(message) shouldBe json
        }
        it("deserialises correctly") {
            Json.decodeFromString<Message>(json) shouldBe message
        }
    }

})
