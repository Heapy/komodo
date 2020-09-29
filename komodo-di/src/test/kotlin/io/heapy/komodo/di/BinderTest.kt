package io.heapy.komodo.di

import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DelegationPatternBinderTest {
    interface Parent
    interface DelegateToParent : Parent
    class ParentImplementation : DelegateToParent

    @Test
    @Disabled
    internal fun test() = runBlockingTest {
        val moduleDelegation by module {
            implementBy<Parent, DelegateToParent>()
            provide<DelegateToParent>(::ParentImplementation)
        }

        val ie1 = createContextAndGet(
            type<Parent>(),
            listOf(moduleDelegation)
        )

        assertTrue(ie1 is ParentImplementation)
    }
}

class BasicBinderTest {
    interface Test1 {
        fun get(): String
    }
    class Test1Impl(
        private val test2: Test2
    ) : Test1 {
        override fun get() = "Test1Impl ${test2.get()}"
    }

    interface Test2 {
        fun get(): String
    }
    class Test2Impl(
        private val test3: Test3
    ) : Test2 {
        override fun get() = "Test2Impl ${test3.get()}"
    }

    class Test3 {
        fun get() = "Test3"
    }

    private suspend fun test2Provider(test3: Test3): Test2 {
        return Test2Impl(test3)
    }

    class TestRoot(
        val t1: Test1,
        val test2: Test2
    ) {
        fun run() = "${t1.get()} ${test2.get()}"
    }

    private val module1 by module {
        provide<Test1>(::Test1Impl)
        provide(::test2Provider)
    }

    private val module2 by module {}

    private val module3 by module {
        provide(::Test3)
        provide(::TestRoot)
    }

    @Test
    fun test() = runBlockingTest {
        val testProvider = createContextAndGet(
            type<Provider<TestRoot>>(),
            listOf(module1, module2, module3)
        )

        val test = testProvider.new()

        assertEquals(
            "Test1Impl Test2Impl Test3 Test2Impl Test3",
            test.run()
        )
    }
}

class OptionalInjectionTest {
    class Foo(
        val bar: Bar?
    )

    class Bar

    private val module1 by module {
        provide(::Foo)
    }

    @Test
    fun `test constructor`() = runBlockingTest {
        val foo = createContextAndGet(
            type<Foo>(),
            listOf(module1)
        )

        assertNull(foo.bar)
    }

    private fun createFoo(bar: Bar?): Foo {
        return Foo(bar)
    }

    private val module2 by module {
        provide(::createFoo)
    }

    @Test
    fun `test provider`() = runBlockingTest {
        val foo = createContextAndGet(
            type<Foo>(),
            listOf(module2)
        )

        assertNull(foo.bar)
    }
}

/**
 * ## Cyclic Dependencies
 *
 * We doesn't support cyclic dependencies,
 * instead of "hacking" classes thought proxies,
 * setters and field injections
 * we require our user to fix their architecture.
 */
class CyclicDependencyTest {
    class Foo(val bar: Bar)
    class Bar(val baz: Baz)
    class Baz(val foo: Foo)

    val cyclic by module {
        provide(::Foo)
        provide(::Bar)
        provide(::Baz)
    }

    @Test
    fun `test cyclic dependencies`() = runBlockingTest {
        val exception = assertThrows<ContextException> {
            runBlockingTest {
                createContextAndGet(
                    type<Foo>(),
                    listOf(cyclic)
                )
            }
        }

        assertEquals("""
            Some Exception here
        """.trimIndent(), exception.message)
    }
}

class ObjectBindingTest {
    object ToBind

    private val obj by module {
        provide { ToBind }
    }

    @Test
    fun `komodo dissallows object binding`() = runBlockingTest {
        val exception = assertThrows<ContextException> {
            runBlockingTest {
                createContextAndGet(
                    type<ToBind>(),
                    listOf(obj)
                )
            }
        }

        assertEquals("""
            Some Exception here
        """.trimIndent(), exception.message)
    }
}
