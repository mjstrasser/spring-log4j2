package mjs.kafka

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class TransactionTest : DescribeSpec({

    fun id() = UUID.randomUUID().toString()
    fun acn() = randomAcn()

    describe("A new Transaction") {
        it("has empty data") {
            Transaction().messages.size shouldBe 0
        }
        it("is incomplete") {
            Transaction().isComplete shouldBe false
        }
    }

    describe("A transaction of one message") {
        it("is complete with that message") {
            Transaction()
                .addMessage(Message(Header(id(), 1, true), Customer(id(), "O")))
                .isComplete shouldBe true
        }
        it("ignores a message that is added more than once") {
            val transId = id()
            val orgId = id()
            val acn = acn()
            Transaction()
                .addMessage(Message(Header(transId, 1, true), Organisation(orgId, acn)))
                .addMessage(Message(Header(transId, 1, true), Organisation(orgId, acn)))
                .isComplete shouldBe true
        }
    }

    describe("A transaction of multiple messages") {
        it("is complete when all messages are added in any order") {
            val transId = id()
            val custId = id()
            listOf(
                Message(Header(transId, 1, false), Customer(custId, "O")),
                Message(Header(transId, 2, false), Organisation(custId, acn())),
                Message(Header(transId, 3, true), OrganisationName(custId, "2021-05-26", "Pickups P/L")),
            ).shuffled()
                .fold(Transaction()) { trans, mess -> trans.addMessage(mess) }
                .isComplete shouldBe true
        }
        it("throws IllegalStateException if a message with a different transaction ID is added") {
            val transId1 = id()
            val transId2 = id()
            val exception = shouldThrow<IllegalStateException> {
                Transaction()
                    .addMessage(Message(Header(transId1, 1, false), Customer(id(), "O")))
                    .addMessage(Message(Header(transId2, 1, true), Customer(id(), "O")))
            }
            exception.message shouldBe "Attempted to add message with transaction ID $transId2 to transaction $transId1"
        }
    }

})
