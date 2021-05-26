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
            val trans = Transaction()
            val transId = id()
            trans.addMessage(Message(Header(transId, 1, true), Customer(id(), "O")))

            trans.isComplete shouldBe true
        }
        it("ignores a message that is added more than once") {
            val trans = Transaction()
            val transId = id()
            val orgId = id()
            val acn = acn()
            trans.addMessage(Message(Header(transId, 1, true), Organisation(orgId, acn)))
            trans.addMessage(Message(Header(transId, 1, true), Organisation(orgId, acn)))

            trans.isComplete shouldBe true
        }
    }

    describe("A transaction of multiple messages") {
        it("is complete when all messages are added in any order") {
            val trans = Transaction()
            val transId = id()
            val custId = id()
            listOf(
                Message(Header(transId, 1, false), Customer(custId, "O")),
                Message(Header(transId, 2, false), Organisation(custId, acn())),
                Message(Header(transId, 3, true), OrganisationName(custId, "2021-05-26", "Pickups P/L")),
            ).shuffled()
                .forEach(trans::addMessage)

            trans.isComplete shouldBe true
        }
        it("throws IllegalStateException if a message with a different transaction ID is added") {
            val trans = Transaction()
            val transId1 = id()
            val transId2 = id()
            val exception = shouldThrow<IllegalStateException> {
                trans.addMessage(Message(Header(transId1, 1, false), Customer(id(), "O")))
                trans.addMessage(Message(Header(transId2, 1, true), Customer(id(), "O")))
            }
            exception.message shouldBe "Attempted to add message with transaction ID $transId2 to transaction $transId1"
        }
    }

})
