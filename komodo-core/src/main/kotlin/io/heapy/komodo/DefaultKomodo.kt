package io.heapy.komodo

import io.heapy.komodo.di.Module
import kotlin.reflect.KClass

/**
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class DefaultKomodo : Komodo {
    private val modules = mutableListOf<Module>()
    private val args = mutableListOf<String>()
    private val env = mutableMapOf<String, String>()
    private val props = mutableMapOf<String, String>()

    override fun module(module: Module): Komodo {
        modules += module
        return this
    }

    override fun args(args: Array<String>): Komodo {
        this.args += args
        return this
    }

    override fun env(env: Map<String, String>): Komodo {
        this.env += env
        return this
    }

    override fun props(props: Map<String, String>): Komodo {
        this.props += props
        return this
    }

    override suspend fun <R> run(entryPoint: KClass<out EntryPoint<R>>): R {
        return entryPoint.objectInstance!!.run()
    }
}
