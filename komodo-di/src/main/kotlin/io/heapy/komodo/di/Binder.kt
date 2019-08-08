package io.heapy.komodo.di

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Interface for defining user modules.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
@ModuleDSL
interface Module

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

//bind(TransactionLog::class.java).to(DatabaseTransactionLog::class.java)
//bind(DatabaseTransactionLog::class.java).to(MySqlDatabaseTransactionLog::class.java)
// get TransactionLog -> MySqlDatabaseTransactionLog

class Test(val t1: Test1, val test2: Test2)

class Test1
class Test2


@ModuleDSL
fun module(builder: Module.() -> Unit): Module {
    return (object : Module {}).also(builder)
}

class BeanDefinition<T : Any>(
    val type: KType
) {
    fun implements(iface: KClass<out T>) {
    }
}

@ExperimentalStdlibApi
@ModuleDSL
inline fun <reified R : Any> Module.create(): BeanDefinition<R> {
    return BeanDefinition(typeOf<R>())
}

@ExperimentalStdlibApi
@ModuleDSL
inline fun <reified R : Any> Module.createList(): BeanDefinition<List<R>> {
    return BeanDefinition(typeOf<List<R>>())
}

inline fun <reified R> Module.create(factory: () -> R) {

}

@ExperimentalStdlibApi
inline fun <reified A : Any, reified R> Module.create(factory: (A) -> R) {
    val t1 = typeOf<A>()
    val r = typeOf<R>()

    println(t1)
    println(r)
}

@ExperimentalStdlibApi
inline fun <reified A : Any, reified R> Module.createExp(factory: (A) -> R) {
    val t1 = typeOf<A>()
    val r = typeOf<R>()

    println(t1)
    println(r)
}

inline fun <reified A : Any, reified B : Any, reified R> Module.create(factory: (A, B) -> R) {

}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified R> Module.create(factory: (A, B, C) -> R) {

}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified R> Module.create(factory: (A, B, C, D) -> R) {

}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified R> Module.create(factory: (A, B, C, D, E) -> R) {

}

@ModuleDSL
fun Module.include(module: Module) {}

data class TestList<T>(
    val list: List<T>
)

val cayenne = module {
}

@ExperimentalStdlibApi
val api = module {
    include(cayenne)
    create<TestList<List<Int>>>()
    createList<Int>()
    create(::Test1)
    create(::Test)
}

@ExperimentalStdlibApi
fun main() {
    val of = mapOf(
        typeOf<List<String>>() to 1,
        typeOf<List<Int>>() to 2,
        typeOf<List<String>>() to 3
    )
}

val service = module {
}

