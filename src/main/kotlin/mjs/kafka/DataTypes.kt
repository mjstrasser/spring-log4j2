package mjs.kafka

import kotlinx.serialization.Serializable

@Serializable
sealed class TableData

@Serializable
data class Customer(
    val id: String,
    val customerType: String
) : TableData()

@Serializable
data class Organisation(
    val customerId: String,
    val acn: String
) : TableData()

@Serializable
data class OrganisationName(
    val customerId: String,
    val createdDate: String,
    val name: String
) : TableData()

@Serializable
data class Header(
    val transactionId: String,
    val transactionEventCounter: Int,
    val lastInTransaction: Boolean = false,
)

@Serializable
data class Wrapper(
    val data: TableData,
    val header: Header
)
