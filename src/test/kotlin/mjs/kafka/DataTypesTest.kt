package mjs.kafka

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataTypesTest : DescribeSpec({

    val transId = randomTxnId()
    val crn = randomCrn()
    val message = Message(
        header = Header(transactionId = transId, transactionEventCounter = 1, transactionLastEvent = true),
        data = Customer(id = crn, customerType = "O"),
    )
    val messageJson =
        """{"header":{"transactionId":"$transId","transactionEventCounter":1,"transactionLastEvent":true},"data":{"type":"mjs.kafka.Customer","id":"$crn","customerType":"O"}}"""

    describe("Wrapped customer message") {
        it("serialises correctly") {
            Json.encodeToString(message) shouldBe messageJson
        }
        it("deserialises correctly") {
            Json.decodeFromString<Message>(messageJson) shouldBe message
        }
    }

    describe("Transaction") {
        val trans = Transaction(transId, setOf(message), 1)
        val transJson =
            """{"id":"$transId","messages":[$messageJson],"partCount":1}"""
        it("is serialised as expected") {
            Json.encodeToString(trans) shouldBe transJson
        }
        it("is deserialised as expected") {
            Json.decodeFromString<Transaction>(transJson) shouldBe trans
        }
    }

})
