package mjs.kafka

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataTypesTest : DescribeSpec({

    describe("Wrapped customer message") {
        it("serialises correctly") {
            val message = Wrapper(
                data = Customer(id = "123456789", customerType = "O"),
                header = Header(transactionId = "123123123", transactionEventCounter = 1),
            )
            Json.encodeToString(message).shouldBe("""{"data":{"type":"mjs.kafka.Customer","id":"123456789","customerType":"O"},"header":{"transactionId":"123123123","transactionEventCounter":1}}""")
        }
        it("deserialises correctly") {
            val json = """{"data":{"type":"mjs.kafka.Customer","id":"123456789","customerType":"O"},"header":{"transactionId":"123123123","transactionEventCounter":1}}"""
            Json.decodeFromString<Wrapper>(json).shouldBe(Wrapper(
                data = Customer(id = "123456789", customerType = "O"),
                header = Header(transactionId = "123123123", transactionEventCounter = 1),
            ))
        }
    }

})
