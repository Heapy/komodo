package io.heapy.komodo.di

import org.koin.ext.getFullName
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

/**
 * Functional type that represents a module.
 * It may be functional interface, but it tend to implement module as classes,
 * what introduces more boilerplate.
 */
public typealias ModuleBuilder = Binder.() -> Unit

public interface Module {
    /**
     * Source of the module (class fqn, or file and variable name).
     * Uniqueness will be enforced by library
     */
    public val source: String

    /**
     * Dependencies of this module
     */
    public val dependencies: List<Module>

    /**
     * Bindings defined in this module
     */
    public val bindings: List<Binding>
}

internal class DefaultModule(
    override val source: String,
    override val dependencies: List<Module>,
    override val bindings: List<Binding>
) : Module

/**
 * Binder used to collect all [Binding]s and create context from them.
 *
 * @author Ruslan Ibragimov
 * @since 0.1.0
 */
public interface Binder {
    public fun dependency(module: ModuleBuilder)
    public fun contribute(binding: Binding)
}


@ModuleDSL
public fun module(builder: ModuleBuilder): ReadOnlyProperty<Any?, ModuleBuilder> {
    return ModuleBuilderDelegate(builder)
}

private class ModuleBuilderDelegate(
    private val builder: ModuleBuilder
) : ReadOnlyProperty<Any?, ModuleBuilder> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): ModuleBuilder {
        println(builder::class.java.name)
        println(property.name) // valResource
        println(builder::class.getFullName()) // io.heapy.komodo.di.BinderKt$valResource$2
        return builder
    }
}

public data class Key(
    val type: KType
)

@DslMarker
public annotation class ModuleDSL

/**
 * Provides instance of type T.
 *
 * @author Ruslan Ibragimov
 * @since 1.0.0
 */
public interface Provider<out T> {
    public suspend fun new(): T
}

public sealed class Binding(
    internal val key: Key
)

public class ClassBinding(
    key: Key,
    internal val classKey: Key,
    internal val instance: Any? = UNDEFINED
) : Binding(key)

internal val UNDEFINED = Any()

public class InstanceBinding(
    key: Key,
    internal val instance: Any?
) : Binding(key)

public class ProviderClassBinding(
    key: Key,
    internal val classKey: Key,
    internal val provider: Provider<*>? = UNDEFINED_PROVIDER
) : Binding(key)

internal val UNDEFINED_PROVIDER = object : Provider<Any> {
    override suspend fun new(): Any = UNDEFINED
}

public class BeanDefinition(
    internal val classKey: Key,
    internal val interfaceKey: Key,
    internal val isProvider: Boolean = false,
    internal var instance: Any? = null,
    internal var provider: Provider<*>? = null
) : Binding(interfaceKey)

public inline class GenericType<T : Any>(
    public val actual: KType
)
public inline fun <reified T : Any> type(): GenericType<T> {
    return GenericType(typeOf<T>())
}

public interface Context {
    public suspend fun <T : Any> get(key: Key): T
    public suspend fun <T : Any> getOrNull(key: Key): T? {
        return try {
            get(key)
        } catch (e: Exception) {
            null
        }
    }
}

internal class KomodoContext(
    private val definitions: Map<Key, Binding>
) : Context {
    override suspend fun <T : Any> get(key: Key): T {
        return if (key.isProvider()) {
            try {
                createType(key, definitions) as T
            } catch (e: Exception) {
                // TODO: Probably this is bad idea
                object : Provider<Any> {
                    override suspend fun new(): Any {
                        return createType(Key(key.type.arguments.first().type!!), definitions) as Any
                    }
                } as T
            }
        } else {
            createType(key, definitions) as T
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun Key.isProvider(): Boolean {
    return type.isSubtypeOf(typeOf<Provider<*>>())
}

public class KomodoBinder : Binder {
    private val definitions = mutableMapOf<Key, Binding>()
    private val modules = mutableSetOf<ModuleBuilder>()

    override fun dependency(module: ModuleBuilder) {
        modules.add(module)
    }

    override fun contribute(binding: Binding) {
        if (!definitions.contains(binding.key)) {
            definitions[binding.key] = binding
        } else {
            throw ContextException("Binding already present")
        }
    }

    public fun createContext(): Context {
        modules.forEach { module ->
            module.invoke(this)
        }
        return KomodoContext(definitions.toMap())
    }
}

public suspend fun <T : Any> createContextAndGet(
    type: GenericType<T>,
    modules: List<ModuleBuilder>
): T {
    val binder = KomodoBinder()
    modules.forEach { module ->
        module.invoke(binder)
    }
    val key = Key(type.actual)
    return binder.createContext().get(key)
}

internal suspend fun createType(
    key: Key,
    definitions: Map<Key, Binding>,
    stack: MutableList<Key> = mutableListOf()
): Any? {
    if (stack.contains(key)) {
        throw ContextException("A circular dependency found: ${printCircularDependencyGraph(key, stack, definitions)}")
    }

    val isOptional = key.type.isMarkedNullable
    val binding = definitions[key] as BeanDefinition?

    return if (binding == null) {
        if (isOptional) {
            null
        } else {
            throw ContextException("Required $key not found in context.")
        }
    } else {
        if (binding.instance != null) {
            return binding.instance
        }

        // TODO: Don't create anything, but just save all actions and metadata,
        //   it will allow to implement dry-run and
        //   and create source code - equivalent of DI
        val type = binding.classKey.type
        val clazz = type.classifier as KClass<*>
        val ctr = clazz.primaryConstructor ?: throw ContextException("The primary constructor is missing.")
        stack.add(key)
        val params = ctr.parameters.associateWith {
            createType(Key(it.type), definitions, stack)
        }
        stack.remove(key)

        val instance = if (binding.isProvider) {
            val cached = binding.provider?.new()
            if (cached == null) {
                val provider = (ctr.callBy(params) as Provider<*>)
                binding.provider = provider
                provider.new()
            } else {
                cached
            }
        } else {
            ctr.callBy(params)
        }

        binding.instance = instance

        instance
    }
}

internal class ContextException(
    override val message: String
) : RuntimeException()

public fun printCircularDependencyGraph(
    key: Key,
    stack: MutableList<Key>,
    bindings: Map<Key, Binding>
): String {
    return buildString {
        appendLine()
        stack.forEachIndexed { idx, stackKey ->
            append(" ".repeat(idx * 2))
            append(stackKey.type.classifier)
            bindings[stackKey]?.let {
                append(" implemented by ")
                val desc = when (it) {
                    is ClassBinding -> "${it.classKey.type.classifier}"
                    is InstanceBinding -> "instance [${it.instance}]"
                    is ProviderClassBinding -> "provider [${it.classKey.type.classifier}]"
                    else -> TODO()
                }
                append(desc)
            }
            if (stackKey == key) {
                appendLine(" <-- Circular dependency starts here")
            } else {
                appendLine()
            }
        }
        append(" ".repeat(stack.size * 2))
        append(key.type.classifier)
        bindings[key]?.let {
            append(" implemented by ")
            val desc = when (it) {
                is ClassBinding -> (it.classKey.type.classifier as KClass<*>).simpleName
                is InstanceBinding -> "instance [${it.instance}]"
                is ProviderClassBinding -> "provider [${it.classKey.type.classifier}]"
                else -> TODO()
            }
            append(desc)
        }
    }
}

// 3. Support override of dependencies (through optional wrapping?, i.e. override particular case of decoration)

// TODO: Provide configuration overview - like what classes overwrite by other classes. modules loaded. etc
// Configuration should be exported as data class
// also add ability to check is context can be started: e.g. validate context
// (List<Binder> -> Context)
// context.validate() -> dry run
// context.inspect() -> context overview
// check that wrappers work with delegation (by impl)

// https://rise4fun.com/agl
// Komodo visualizer site -> past json(dot files?) from terminal to visualize dependency graph!!!

// Scopes
// We support only singleton scope, since other scopes can be implemented in user space based on singleton scope
// And session/request scope should be managed by underline request framework.
// Inject Provider of any bean (useful for custom scopes)

// start/stop functions
// order problem: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/DependsOn.html

// warmUp function - top level function that can be executed before server will be started

@ModuleDSL
public inline fun <reified I : Any, reified C : I> Binder.implementBy(
    customizer: (BeanDefinition) -> BeanDefinition = { it }
) {
    BeanDefinition(
        classKey = Key(typeOf<C>()),
        interfaceKey = Key(typeOf<I>())
    ).also {
        contribute(customizer(it))
    }
}

@ModuleDSL
public inline fun <reified I : Any> Binder.provide(
    noinline provider: suspend () -> I
) {
    // is provider suspend?
    BeanDefinition(
        classKey = Key(typeOf<I>()),
        interfaceKey = Key(typeOf<I>()),
        isProvider = true
    ).also {
        contribute(it)
    }
}

@ModuleDSL
public inline fun <reified I : Any> Binder.provide(
    provider: KFunction<I>,
    customizer: (BeanDefinition) -> BeanDefinition = { it }
) {
    // is provider suspend?
    BeanDefinition(
        classKey = Key(typeOf<I>()),
        interfaceKey = Key(typeOf<I>()),
        isProvider = true
    ).also {
        contribute(customizer(it))
    }
}

@ModuleDSL
public inline fun <reified I : Any> Binder.provideList(
    provider: KFunction<I>,
    customizer: (BeanDefinition) -> BeanDefinition = { it }
) {
    // is provider suspend?
    BeanDefinition(
        classKey = Key(typeOf<I>()),
        interfaceKey = Key(typeOf<I>()),
        isProvider = true
    ).also {
        contribute(customizer(it))
    }
}
