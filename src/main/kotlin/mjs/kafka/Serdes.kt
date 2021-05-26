package mjs.kafka

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

/**
 * Kafka [Serializer] for a Kotlin type that can be serialised using
 * `kotlinx.serialization`.
 */
inline fun <reified T> serializerFor() =
    Serializer<T> { _, data -> Json.encodeToString(data).toByteArray() }

/**
 * Kafka [Deserializer] for a Kotlin type that can be serialised using
 * `kotlinx.serialization`.
 */
inline fun <reified T> deserializerFor() =
    Deserializer { _, data -> Json.decodeFromString<T>(String(data)) }

/**
 * Kafka [Serde] for a Kotlin type that can be serialised using
 * `kotlinx.serialization`.
 */
inline fun <reified T> serdeFor() = object : Serde<T> {
    override fun serializer() = serializerFor<T>()
    override fun deserializer() = deserializerFor<T>()
}
