package io.heapy.komodo.di.default

import io.heapy.komodo.di.Binder
import io.heapy.komodo.di.BindingBuilder
import io.heapy.komodo.di.Module
import io.heapy.komodo.di.Provider
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

// TODO: Provide configuration overview - like what classes overwrite by other classes. modules loaded. etc
// Configuration should be exported as data class
// also add ability to check is context can be started: e.g. validate context
// (List<Binder> -> Context)
// context.validate() -> dry run
// context.inspect() -> context overview
// check that wrappers work with delegation (by impl)

// Komodo visualizer site -> past json from terminal to visualize dependency graph!!!

// Scopes
// We support only singleton scope, since other scopes can be implemented in user space based on singleton scope
// And session/request scope should be managed by underline request framework.

// Cyclic Dependencies
// We doesn't support cyclic dependencies, instead of "hacking" classes thought proxies, setters and field injections
// we require our user to fix their architecture.

// Optional Dependencies
// Implemented thought nullable reference in constructor
// class Foo(val bar: Bar?)
// bar - is optional dependency

/**
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class KomodoBinder(
    private val dependencies: MutableMap<KClass<*>, Dependency<*>> = mutableMapOf()
) : Binder {
    override fun <T : Any> wire(klass: KClass<T>): BindingBuilder<T> {
        dependencies.put(klass, ClassDependency(klass, klass))
        return KomodoBindingBuilder(this, klass)
    }

    fun <T : Any> to(interfaceClass: KClass<T>, klass: KClass<out T>) {
        dependencies.put(interfaceClass, ClassDependency(interfaceClass, klass))
    }

    fun <T : Any> to(interfaceClass: KClass<T>, impl: T) {
        dependencies.put(interfaceClass, ImplDependency(interfaceClass, impl))
    }

    fun <T : Any> toProvider(interfaceClass: KClass<T>, provider: KClass<out Provider<T>>) {
        dependencies.put(interfaceClass, ProviderDependency(interfaceClass, provider))
    }

    fun <T : Any> decorate(interfaceClass: KClass<T>, klass: KClass<out T>) {
        val dependency = getDependency(interfaceClass)
        dependencies.put(interfaceClass, WrappedClassDependency(interfaceClass, klass, dependency))
    }

    fun <T : Any> decorate(interfaceClass: KClass<T>, impl: T) {
        val dependency = getDependency(interfaceClass)
        dependencies.put(interfaceClass, WrappedImplDependency(interfaceClass, impl, dependency))
    }

    fun <T : Any> decorateWithProvider(interfaceClass: KClass<T>, provider: KClass<out Provider<T>>) {
        val dependency = getDependency(interfaceClass)
        dependencies.put(interfaceClass, WrappedProviderDependency(interfaceClass, provider, dependency))
    }

    fun <T : Any> override(interfaceClass: KClass<T>, moduleKlass: KClass<out Module>) {
        val dependency = getDependency(interfaceClass)
        dependencies.put(interfaceClass, DependencyOverride(interfaceClass, moduleKlass, dependency))
    }

    private fun <T : Any> getDependency(interfaceClass: KClass<T>): Dependency<T> {
        @Suppress("UNCHECKED_CAST")
        val dependency = dependencies[interfaceClass] as? Dependency<T>

        return dependency ?: throw KomodoBindingException(
            message = """Interface "${interfaceClass.simpleName}" not found in binder."""
        )
    }

    suspend fun <T : Any> build(root: KClass<out T>): T {
        val dep = dependencies[root]
        return when (dep) {
            is ClassDependency -> {
                val k = dep.clazz
                val c = k.constructor
                val params = c.parameters

                val obj = try {
                    if (params.isEmpty()) {
                        c.call()
                    } else {
                        val paramsToInstance = params.associateBy(
                            { it },
                            { build(it.type.jvmErasure) }
                        )

                        c.callBy(paramsToInstance)
                    }
                } catch (e: Exception) {
                    throw e
                }

                return if (obj is Provider<*>) {
                    obj.getInstance() as T
                } else {
                    obj as T
                }
            }
            is ImplDependency -> {
                dep.impl as T
            }
            else -> throw RuntimeException("HAHAH")
//            is DependencyOverride -> {
//
//            }
//            is ProviderDependency -> {
//                dep.provider.constructor
//            }
//            is DependencySet -> {
//                dep.dependencies.map {  }
//            }
//            is WrappedImplDependency -> {
//                dep.impl
//            }
//            is WrappedProviderDependency -> {
//
//            }
//            is WrappedClassDependency -> {
//
//            }
//            null -> {
//
//            }
        }
    }

    private val modules = mutableMapOf<KClass<out Module>, Boolean>()

//    tailrec fun module(module: Module) {
//        val instance = moduleInstance(module)
//
//        instance?.let {
//            it.assemble(this)
//            it.requires.forEach { module(it.createInstance()) }
//        }
//    }

    private fun moduleInstance(module: Module): Module? {
        return null
//        return when (module) {
//            is ModuleClass -> {
//                val klass = module.klass
//                if (modules.contains(klass)) null else klass.constructor.call()
//            }
//            is ModuleInstance -> {
//                val instance = module.instance
//                val klass = instance::class
//                if (modules.contains(klass)) null else instance
//            }
//        }
    }
}

private fun <T : Any> KClass<T>.createInstance(): T {
    val constructor = this.constructor
    val params = constructor.parameters

    if (params.isEmpty()) {
        return constructor.call()
    } else {
        val args = params.associate { it to "" }

        return constructor.callBy(args)
    }
}

/**
 * Returns single constructor or throws exception otherwise
 */
private val <T : Any> KClass<T>.constructor
    get(): KFunction<T> {
        val constructor = this.primaryConstructor
        return constructor ?: throw KomodoBindingException(
            message = """Expected primary constructor for type "${this.qualifiedName}"."""
        )
    }

class Injections(
    private val injections: Map<KClass<*>, Any>
) {
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(clazz: KClass<T>): T {
        return (injections[clazz] ?: throw RuntimeException("Nope!")) as? T ?: throw RuntimeException("Type!")
    }
}
