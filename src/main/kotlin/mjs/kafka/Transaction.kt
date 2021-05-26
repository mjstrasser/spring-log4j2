package mjs.kafka

import org.apache.logging.log4j.kotlin.Logging
import kotlin.math.max

class Transaction(
    private val id: String? = null,
    val messages: Set<Message> = setOf(),
    private val partCount: Int = -1,
) : Logging {
    val isComplete: Boolean
        get() = messages.size == partCount

    fun addMessage(message: Message): Transaction {

        if (id != null && id != message.header.transactionId)
            throw IllegalStateException("Attempted to add message with transaction ID ${message.header.transactionId} to transaction $id")

        return Transaction(
            message.header.transactionId,
            messages + message,
            if (message.header.transactionLastEvent)
                message.header.transactionEventCounter
            else
                max(partCount, -1)
        )
    }
}
