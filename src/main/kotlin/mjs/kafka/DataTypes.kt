package mjs.kafka

import kotlinx.serialization.Serializable

@Serializable
data class Customer(val id: String, val type: String)

@Serializable
data class Organisation(val customerId: String, val acn: String)

@Serializable
data class OrganisationName(val customerId: String, val createdDate: String, val name: String)
