package io.heapy.komodo

import io.heapy.komodo.di.BeanDefinition
import io.heapy.komodo.di.Key
import io.heapy.komodo.di.Module
import io.heapy.komodo.di.ModuleBuilder
import io.heapy.komodo.di.bindConcrete
import kotlin.reflect.full.createInstance
import io.heapy.komodo.di.createContextAndGet
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class DefaultKomodoBuilder : KomodoBuilder {
    private val modules = mutableListOf<KClass<out Module>>()
    private val moduleInstances = mutableListOf<Module>()
    private val args = mutableListOf<String>()
    private val env = mutableMapOf<String, String>()
    private val props = mutableMapOf<String, String>()

    override fun module(module: KClass<out Module>) {
        modules += module
    }

    override fun module(module: Module) {
        moduleInstances += module
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

    fun komodo(): Komodo {
        return DefaultKomodo(
            modules = modules.toList(),
            moduleInstances = moduleInstances.toList(),
            env = env.toMap(),
            args = args.toTypedArray(),
            props = props.toMap()
        )
    }
}

@KomodoDsl
class DefaultKomodo(
    private val modules: List<KClass<out Module>>,
    private val moduleInstances: List<Module>,
    private val env: Map<String, String>,
    private val args: Array<String>,
    private val props: Map<String, String>
) : Komodo {
    override suspend fun run(type: KType) {
        runReturning<Unit>(type)
    }

    override suspend fun <R> runReturning(type: KType): R {
        val moduleInstances = modules
            .asSequence()
            .map { module -> createModuleInstance(module) }
            .plus(moduleInstances)
            .plus(object : Module {
                override fun getBindings(): ModuleBuilder = {
                    val key = Key(type)
                    contribute(BeanDefinition<EntryPoint<R>, EntryPoint<R>>(
                        classKey = key,
                        interfaceKey = key
                    ))
                }
            })
        return createContextAndGet<EntryPoint<R>>(type, moduleInstances.toList()).run()
    }

    private fun createModuleInstance(module: KClass<out Module>): Module {
        return module.createInstance()
    }
}

suspend fun main() {
    komodo<Application> {
        module {
            bindConcrete<Service1>()
            bindConcrete<Service2>()
        }
    }
}

class Application(
    private val service1: Service1
) : UnitEntryPoint {
    override suspend fun run() {
        service1.run()
    }
}

class Service1(private val service2: Service2) {
    fun run() {
        service2.hello()
    }
}

class Service2 {
    fun hello() {
        println("Hello, World!")
    }
}

class K1 : Module {
    override fun getBindings(): ModuleBuilder = {
        bindConcrete<Service1>()
    }
}

class K2 : Module {
    override fun getBindings(): ModuleBuilder = {
        bindConcrete<Service2>()
    }
}
