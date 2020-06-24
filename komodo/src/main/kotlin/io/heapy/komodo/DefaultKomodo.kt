package io.heapy.komodo

import io.heapy.komodo.di.BeanDefinition
import io.heapy.komodo.di.GenericType
import io.heapy.komodo.di.Key
import io.heapy.komodo.di.ModuleBuilder
import io.heapy.komodo.di.createContextAndGet
import io.heapy.komodo.di.module

/**
 * @author Ruslan Ibragimov
 * @since 1.0
 */
@PublishedApi
internal class DefaultKomodoBuilder : KomodoBuilder {
    private val modules = mutableListOf<ModuleBuilder>()
    private val args = mutableListOf<String>()
    private val env = mutableMapOf<String, String>()
    private val props = mutableMapOf<String, String>()

    override fun module(module: ModuleBuilder) {
        modules += module
    }

    override fun args(args: Array<String>) {
        this.args += args
    }

    override fun env(env: Map<String, String>) {
        this.env += env
    }

    override fun props(props: Map<String, String>) {
        this.props += props
    }

    @PublishedApi
    internal fun komodo(): Komodo {
        return DefaultKomodo(
            modules = modules.toList(),
            env = env.toMap(),
            args = args.toTypedArray(),
            props = props.toMap()
        )
    }
}

@PublishedApi
internal interface Komodo {
    suspend fun <T : EntryPoint<R>, R> run(type: GenericType<T>): R
}

@KomodoDsl
internal class DefaultKomodo(
    private val modules: List<ModuleBuilder>,
    private val env: Map<String, String>,
    private val args: Array<String>,
    private val props: Map<String, String>
) : Komodo {
    override suspend fun <T : EntryPoint<R>, R> run(type: GenericType<T>): R {
        val moduleInstances = modules
            .asSequence()
            .plus(module {
                val key = Key(type.actual)
                contribute(
                    BeanDefinition(
                        classKey = key,
                        interfaceKey = key
                    )
                )
            })
        return createContextAndGet(type, moduleInstances.toList()).run()
    }
}
