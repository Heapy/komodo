package io.heapy.komodo

import io.heapy.komodo.di.Module
import io.heapy.komodo.di.ModuleBuilder
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Collects all external dependencies to build instance of [Komodo].
 */
@KomodoDsl
interface KomodoBuilder {
    @KomodoDsl
    fun args(args: Array<String>)

    @KomodoDsl
    fun env(env: Map<String, String>)

    @KomodoDsl
    fun props(props: Map<String, String>)

    @KomodoDsl
    fun module(module: KClass<out Module>)

    @KomodoDsl
    fun module(module: Module)
}

interface Komodo {
    suspend fun run(type: KType)
    suspend fun <R> runReturning(type: KType): R
}

@DslMarker
annotation class KomodoDsl

@KomodoDsl
suspend inline fun <reified T : UnitEntryPoint> komodo(
    builder: KomodoBuilder.() -> Unit
) {
    val komodoBuilder = DefaultKomodoBuilder()
    builder(komodoBuilder)
    val komodo = komodoBuilder.komodo()
    komodo.run(typeOf<T>())
}

fun KomodoBuilder.module(builder: ModuleBuilder) {
    module(object : Module {
        override fun getBindings(): ModuleBuilder {
            return builder
        }
    })
}

@KomodoDsl
suspend inline fun <reified R> komodoReturning(
    builder: KomodoBuilder.() -> Unit
): R {
    val komodoBuilder = DefaultKomodoBuilder()
    builder(komodoBuilder)
    val komodo = komodoBuilder.komodo()
    return komodo.runReturning(typeOf<R>())
}
