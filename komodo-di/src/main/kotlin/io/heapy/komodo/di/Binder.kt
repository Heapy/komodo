package io.heapy.komodo.di

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

/**
 * Interface for defining user modules.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
@ModuleDSL
interface Module {
    val bindings: Map<Key, BeanDefinition<*>>
    fun add(definition: BeanDefinition<*>)
}

class DefaultModule : Module {
    override val bindings: MutableMap<Key, BeanDefinition<*>> = mutableMapOf()

    override fun add(definition: BeanDefinition<*>) {
        bindings[definition.key] = definition
    }
}

class BeanDefinition<T : Any>(
    val key: Key
) {
    fun implements(iface: KClass<out T>) {
    }
}

inline fun <reified T : Any> buildContextAndGet(modules: List<Module>): T {
    val bindings = modules.fold(mutableMapOf<Key, BeanDefinition<*>>()) { acc, module ->
        acc.putAll(module.bindings)
        acc
    }

    val key = Key(typeOf<T>())
    val beanDefinition = bindings[key]
    return createType(beanDefinition!!.key.type, bindings) as T
}

fun createType(type: KType, bindings: Map<Key, BeanDefinition<*>>): Any {
    val optional = type.isMarkedNullable

    val ctr = type.jvmErasure.primaryConstructor!!
    val params = ctr.parameters.map { createType(it.type, bindings) }.toTypedArray()
    return ctr.call(*params)
}

data class Key(
    val type: KType,
    val qualifier: Qualifier = DefaultQualifier
)

interface Qualifier

object DefaultQualifier : Qualifier

@DslMarker
annotation class ModuleDSL

/**
 * We need Provider interface until issue with reflection for lambda parameters not resolved.
 * https://youtrack.jetbrains.com/issue/KT-9062
 * Than we can use lambdas instead.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface Provider<out T> {
    suspend fun getInstance(): T
}


// 1. Inject Provider of any bean (useful for custom scopes)
// 2. Optional injection (for top-level types with `?`)
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

// Cyclic Dependencies
// We doesn't support cyclic dependencies, instead of "hacking" classes thought proxies, setters and field injections
// we require our user to fix their architecture.

// Optional Dependencies
// Implemented thought nullable reference in constructor
// class Foo(val bar: Bar?)
// bar - is optional dependency


//bind(TransactionLog::class.java).to(DatabaseTransactionLog::class.java)
//bind(DatabaseTransactionLog::class.java).to(MySqlDatabaseTransactionLog::class.java)
// get TransactionLog -> MySqlDatabaseTransactionLog

@ModuleDSL
fun module(builder: Module.() -> Unit): Module {
    return (DefaultModule()).also(builder)
}

@ModuleDSL
fun Module.include(vararg module: Module) {
}

@ModuleDSL
inline fun <reified R : Any> Module.create() {
    add(BeanDefinition<R>(key = Key(typeOf<R>())))
}

@ModuleDSL
inline fun <reified R : Any> Module.createList() {
    add(BeanDefinition<R>(key = Key(typeOf<List<R>>())))
}

inline fun <reified R> Module.create(factory: () -> R) {

}

inline fun <reified A : Any, reified R> Module.create(factory: (A) -> R) {

}

inline fun <reified A : Any, reified B : Any, reified R> Module.create(factory: (A, B) -> R) {

}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified R> Module.create(factory: (A, B, C) -> R) {

}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified R> Module.create(factory: (A, B, C, D) -> R) {

}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified R> Module.create(factory: (A, B, C, D, E) -> R) {

}

data class TestList<T>(
    val list: List<T>
)

class Test1
class Test2
class Test(val t1: Test1, val test2: Test2)

val api = module {
    createList<Int>()
    create(::Test1)
    create(::Test)
}

fun main() {
    val m1 = module {
        create<Test1>()
    }

    val m2 = module {
        create<Test2>()
    }

    val m3 = module {
        create<Test>()
    }

    val test = buildContextAndGet<Test>(listOf(m1, m2, m3))
    println(test.t1)
    println(test.test2)
}
