package mjs.kafka

import kotlinx.serialization.Serializable
import org.apache.logging.log4j.kotlin.Logging
import kotlin.math.max

@Serializable
data class Transaction(
    val id: String? = null,
    val messages: Set<Message> = setOf(),
    private val eventCount: Int = -1,
) : Logging {
    val isComplete: Boolean
        get() = messages.size == eventCount

    fun addMessage(message: Message): Transaction {

        logger.debug { "++ Transaction.addMessage($message)" }

        if (id != null && id != message.header.transactionId)
            throw IllegalStateException("Attempted to add message with transaction ID ${message.header.transactionId} to transaction $id")

        return Transaction(
            message.header.transactionId,
            messages + message,
            if (message.header.transactionLastEvent)
                message.header.transactionEventCounter
            else
                max(eventCount, -1)
        )
    }
}
