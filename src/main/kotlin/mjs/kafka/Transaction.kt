package mjs.kafka

const val NONE = "NONE"

class Transaction {
    var id: String = NONE
    val messages = mutableSetOf<Message>()
    private var partCount: Int = -1
    val isComplete: Boolean
        get() = messages.size == partCount

    fun addMessage(message: Message): Transaction {

        if (id == NONE)
            id = message.header.transactionId
        else if (id != message.header.transactionId)
            throw IllegalStateException("Attempted to add message with transaction ID ${message.header.transactionId} to transaction $id")

        messages.add(message)

        if (message.header.lastInTransaction) {
            partCount = message.header.transactionEventCounter
        }

        return this
    }
}