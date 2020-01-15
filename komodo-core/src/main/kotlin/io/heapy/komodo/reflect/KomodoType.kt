package io.heapy.komodo.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Represents [KType] or [KClass].
 * Both make sense, so common type useful for providing metadata.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
sealed class KomodoType<T>

class KomodoKClass<T : Any>(val clazz: KClass<T>) : KomodoType<T>() {
    companion object
}

inline fun <reified T : Any> KomodoKClass.Companion.from() = KomodoKClass(T::class)

class KomodoKType<T : Any>(val type: KType) : KomodoType<T>() {
    companion object
}

inline fun <reified T : Any> KomodoKType.Companion.from() = KomodoKType<T>(typeOf<T>())
