package io.heapy.komodo.config

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Marker Interface for configuration classes.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public interface KomodoConfiguration {
    public suspend fun <T : Any> getConfig(type: KType): T?
}

public suspend inline fun <reified T : Any> KomodoConfiguration.getConfig(): T? {
    return getConfig(typeOf<T>())
}
