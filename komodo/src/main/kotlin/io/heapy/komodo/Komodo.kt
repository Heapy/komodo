package io.heapy.komodo

import io.heapy.komodo.di.ModuleBuilder
import io.heapy.komodo.di.type

/**
 * Collects all external dependencies to build instance of [Komodo].
 */
@KomodoDsl
public interface KomodoBuilder {
    @KomodoDsl
    public fun args(args: Array<String>)

    @KomodoDsl
    public fun env(env: Map<String, String>)

    @KomodoDsl
    public fun props(props: Map<String, String>)

    @KomodoDsl
    public fun module(module: ModuleBuilder)
}

// TODO: How to use DSL marker properly?
@DslMarker
internal annotation class KomodoDsl

@KomodoDsl
public suspend inline fun <reified T : EntryPoint<Unit>> komodo(
    builder: KomodoBuilder.() -> Unit
) {
    komodoReturning<T, Unit>(builder)
}

@KomodoDsl
public suspend inline fun <reified T : EntryPoint<R>, R> komodoReturning(
    builder: KomodoBuilder.() -> Unit
): R? {
    val komodoBuilder = DefaultKomodoBuilder()
    builder(komodoBuilder)
    val komodo = komodoBuilder.komodo()
    return komodo.run(type<T>())
}
