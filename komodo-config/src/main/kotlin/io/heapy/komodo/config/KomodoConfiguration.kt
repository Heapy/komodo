package io.heapy.komodo.config

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Marker Interface for configuration classes.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface KomodoConfiguration {
    suspend fun <T : Any> getConfig(type: KType): T?
}

suspend inline fun <reified T : Any> KomodoConfiguration.getConfig(): T? {
    return getConfig(typeOf<T>())
}
