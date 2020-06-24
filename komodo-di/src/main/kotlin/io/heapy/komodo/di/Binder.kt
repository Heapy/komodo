package io.heapy.komodo.di

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

internal class ContextException(override val message: String) : RuntimeException()

public typealias ModuleBuilder = Binder.() -> Unit

/**
 * Binder used to collect all [BeanDefinition]s and create context later.
 *
 * @author Ruslan Ibragimov
 * @since 0.1.0
 */
public interface Binder {
    public fun dependency(module: ModuleBuilder)
    public fun contribute(definition: BeanDefinition)
}

public class BeanDefinition(
    internal val classKey: Key,
    internal val interfaceKey: Key,
    internal val isProvider: Boolean = false,
    internal var instance: Any? = null,
    internal var provider: Provider<*>? = null
)

public class BeanDefinitionBak<I : Any, C : I>(
    internal val list: Boolean = false,
    internal var wrapper: KType? = null
) {
    internal var initMethod: (C.() -> Any)? = null

    @ModuleDSL
    public fun initMethod(function: C.() -> Any): BeanDefinitionBak<I, C> {
        initMethod = function
        return this
    }

    public fun wrap(wrapper: I): BeanDefinitionBak<I, C> {
        return this
    }
}

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

public class KomodoContext(
    private val definitions: Map<Key, BeanDefinition>
) : Context {
    override suspend fun <T : Any> get(key: Key): T {
        return if (key.type.isSubtypeOf(typeOf<Provider<*>>())) {
            try {
                createType(key, definitions) as T
            } catch (e: Exception) {
                object : Provider<Any> {
                    override suspend fun getInstance(): Any {
                        return createType(Key(key.type.arguments.first().type!!), definitions) as Any
                    }
                } as T
            }
        } else {
            createType(key, definitions) as T
        }
    }
}

public class KomodoBinder : Binder {
    private val definitions = mutableMapOf<Key, BeanDefinition>()
    override fun dependency(module: ModuleBuilder) {
        module(this)
    }

    override fun contribute(definition: BeanDefinition) {
        definitions[definition.interfaceKey] = definition
    }

    public fun createContext(): Context {
        return KomodoContext(definitions.toMap())
    }
}

public suspend fun <T : Any> createContextAndGet(
    type: GenericType<T>,
    modules: List<ModuleBuilder>
): T {
    val binder = KomodoBinder()
    modules.forEach { module -> module.invoke(binder) }
    val key = Key(type.actual)
    return binder.createContext().get(key)
}

public suspend fun createType(
    key: Key,
    definitions: Map<Key, BeanDefinition>,
    stack: MutableList<Key> = mutableListOf()
): Any? {
    if (stack.contains(key)) {
        throw ContextException("A circular dependency found: ${printCircularDependencyGraph(key, stack, definitions)}")
    }

    val isOptional = key.type.isMarkedNullable
    val binding = definitions[key]

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
            val cached = binding.provider?.getInstance()
            if (cached == null) {
                val provider = (ctr.callBy(params) as Provider<*>)
                binding.provider = provider
                provider.getInstance()
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

public fun printCircularDependencyGraph(
    key: Key,
    stack: MutableList<Key>,
    definitions: Map<Key, BeanDefinition>
): String {
    return buildString {
        appendln()
        stack.forEachIndexed { idx, stackKey ->
            append(" ".repeat(idx * 2))
            append(stackKey.type.classifier)
            definitions[stackKey]?.let {
                append(" implemented by ")
                append(it.classKey.type.classifier)
            }
            if (stackKey == key) {
                appendln(" <-- Circular dependency starts here")
            } else {
                appendln()
            }
        }
        append(" ".repeat(stack.size * 2))
        append(key.type.classifier)
        definitions[key]?.let {
            append(" implemented by ")
            append((it.classKey.type.classifier as KClass<*>).simpleName)
        }
    }
}

public data class Key(
    val type: KType,
    val qualifier: Qualifier = DefaultQualifier
)

public interface Qualifier

public object DefaultQualifier : Qualifier

@DslMarker
public annotation class ModuleDSL

/**
 * We need Provider interface until issue with reflection for lambda parameters not resolved.
 * https://youtrack.jetbrains.com/issue/KT-9062
 * Than we can use lambdas instead.
 *
 * @author Ruslan Ibragimov
 * @since 1.0.0
 */
public interface Provider<out T> {
    public suspend fun getInstance(): T
}

// 3. Support override of dependencies (through optional wrapping?, i.e. override particular case of decoration)

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
// Inject Provider of any bean (useful for custom scopes)

// Cyclic Dependencies
// We doesn't support cyclic dependencies, instead of "hacking" classes thought proxies, setters and field injections
// we require our user to fix their architecture.

// Optional Dependencies
// Implemented thought nullable reference in constructor
// class Foo(val bar: Bar?)
// bar - is optional dependency

// start/stop functions
// order problem: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/DependsOn.html

// warmUp function - top level function that can be executed before server will be started

//bind(TransactionLog::class.java).to(DatabaseTransactionLog::class.java)
//bind(DatabaseTransactionLog::class.java).to(MySqlDatabaseTransactionLog::class.java)
// get TransactionLog -> MySqlDatabaseTransactionLog

@ModuleDSL
public fun module(builder: ModuleBuilder): ModuleBuilder {
    println(builder::class.java.name)
    return builder
}

@ModuleDSL
public inline fun <reified C : Any> Binder.bindConcrete(): BeanDefinition {
    val classKey = Key(typeOf<C>())
    return BeanDefinition(
        classKey = classKey,
        interfaceKey = classKey
    ).also {
        contribute(it)
    }
}

@ModuleDSL
public inline fun <reified I : Any, reified C : I> Binder.bind(): BeanDefinition {
    return BeanDefinition(
        classKey = Key(typeOf<C>()),
        interfaceKey = Key(typeOf<I>())
    ).also {
        contribute(it)
    }
}

@ModuleDSL
public inline fun <reified I : Any, reified P : Provider<I>> Binder.provide(): BeanDefinition {
    return BeanDefinition(
        classKey = Key(typeOf<P>()),
        interfaceKey = Key(typeOf<I>()),
        isProvider = true
    ).also {
        contribute(it)
    }
}

@ModuleDSL
public inline fun <reified R : Any> Binder.createList() {
//    contribute(BeanDefinition<R, R>(
//        key = Key(typeOf<List<R>>()),
//        iface = R::class,
//        list = true
//    ))
}
