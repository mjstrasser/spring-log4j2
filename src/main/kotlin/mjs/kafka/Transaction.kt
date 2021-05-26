package mjs.kafka

const val NONE = "NONE"

class Transaction(
    private val id: String = NONE,
    val messages: Set<Message> = setOf(),
    private val partCount: Int = -1,
) {
    val isComplete: Boolean
        get() = messages.size == partCount

    fun addMessage(message: Message): Transaction {

        if (id != NONE && id != message.header.transactionId)
            throw IllegalStateException("Attempted to add message with transaction ID ${message.header.transactionId} to transaction $id")

        return Transaction(
            id = message.header.transactionId,
            messages = messages + message,
            partCount = if (message.header.lastInTransaction) message.header.transactionEventCounter else -1
        )
    }
}