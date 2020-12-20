package io.heapy.komodo

import io.heapy.komodo.di.Binding
import io.heapy.komodo.di.GenericType
import io.heapy.komodo.di.Module
import io.heapy.komodo.di.ModuleProvider
import io.heapy.komodo.di.createContextAndGet
import io.heapy.komodo.di.module
import io.heapy.komodo.di.provide

/**
 * @author Ruslan Ibragimov
 * @since 1.0
 */
@PublishedApi
internal class DefaultKomodoBuilder : KomodoBuilder {
    private val modules = mutableListOf<ModuleProvider>()
    private val bindings = mutableListOf<Binding>()
    private val args = mutableListOf<String>()
    private val env = mutableMapOf<String, String>()
    private val props = mutableMapOf<String, String>()

    override fun args(args: Array<String>) {
        this.args += args
    }

    override fun env(env: Map<String, String>) {
        this.env += env
    }

    override fun props(props: Map<String, String>) {
        this.props += props
    }

    override fun dependency(module: ModuleProvider) {
        modules += module
    }

    override fun contribute(binding: Binding) {
        bindings += binding
    }

    @PublishedApi
    internal fun komodo(): Komodo {
        return DefaultKomodo(
            modules = modules.toList(),
            env = env.toMap(),
            args = args.toTypedArray(),
            props = props.toMap(),
            bindings = bindings.toList()
        )
    }
}

@PublishedApi
internal interface Komodo {
    suspend fun <T : EntryPoint<R>, R> run(source: String, type: GenericType<T>): R
}

internal class EnvMap(val env: Map<String, String>)
internal class PropMap(val props: Map<String, String>)
internal class ArgList(val args: Array<String>)

@KomodoDsl
internal class DefaultKomodo(
    private val modules: List<ModuleProvider>,
    private val bindings: List<Binding>,
    private val env: Map<String, String>,
    private val args: Array<String>,
    private val props: Map<String, String>
) : Komodo {
    override suspend fun <T : EntryPoint<R>, R> run(source: String, type: GenericType<T>): R {
        val komodoModule by module {
            provide({ EnvMap(env) })
            provide({ PropMap(props) })
            provide({ ArgList(args) })
        }

        val komodoModuleProvider = {
            object : Module {
                override val source = source
                override val dependencies = modules + komodoModule
                override val bindings = this@DefaultKomodo.bindings
            }
        }

        return createContextAndGet(type, komodoModuleProvider).run()
    }
}
