package mjs.kafka

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TransactionTest : DescribeSpec({

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
                .addMessage(Message(Header(randomKey(), 1, true), Customer(randomKey(), "O")))
                .isComplete shouldBe true
        }
        it("ignores a message that is added more than once") {
            val transId = randomKey()
            val orgId = randomKey()
            val acn = randomAcn()
            Transaction()
                .addMessage(Message(Header(transId, 1, true), Organisation(orgId, acn)))
                .addMessage(Message(Header(transId, 1, true), Organisation(orgId, acn)))
                .messages.size shouldBe 1
        }
    }

    describe("A transaction of multiple messages") {
        it("is complete when all messages are added in any order") {
            val transId = randomKey()
            val custId = randomKey()
            listOf(
                Message(Header(transId, 1, false), Customer(custId, "O")),
                Message(Header(transId, 2, false), Organisation(custId, randomAcn())),
                Message(Header(transId, 3, true), OrganisationName(custId, "2021-05-26", "Pickups P/L")),
            ).shuffled()
                .fold(Transaction()) { trans, mess -> trans.addMessage(mess) }
                .isComplete shouldBe true
        }
        it("throws IllegalStateException if a message with a different transaction ID is added") {
            val transId1 = randomKey()
            val transId2 = randomKey()
            val exception = shouldThrow<IllegalStateException> {
                Transaction()
                    .addMessage(Message(Header(transId1, 1, false), Customer(randomKey(), "O")))
                    .addMessage(Message(Header(transId2, 1, true), Customer(randomKey(), "O")))
            }
            exception.message shouldBe "Attempted to add message with transaction ID $transId2 to transaction $transId1"
        }
    }

})
