package mjs.kafka

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

inline fun <reified T> serializerFor() =
    Serializer<T> { _, data -> Json.encodeToString(data).toByteArray() }

inline fun <reified T> deserializerFor() =
    Deserializer { _, data -> Json.decodeFromString<T>(String(data)) }

inline fun <reified T> serdeFor() = object : Serde<T> {
    override fun serializer() = serializerFor<T>()
    override fun deserializer() = deserializerFor<T>()
}
