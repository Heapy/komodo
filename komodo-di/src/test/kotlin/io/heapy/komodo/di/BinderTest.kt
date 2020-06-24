package io.heapy.komodo.di

import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class DelegationPatternBinderTest {
    interface Parent
    interface DelegateToParent : Parent
    class ParentImplementation : DelegateToParent

    @Test
    @Disabled
    internal fun test() = runBlockingTest {
        val moduleDelegation = module {
            bind<Parent, DelegateToParent>()
            bind<DelegateToParent, ParentImplementation>()
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

    class Test2Provider(
        private val test3: Test3
    ) : Provider<Test2> {
        override suspend fun getInstance(): Test2 {
            return Test2Impl(test3)
        }
    }

    class TestRoot(
        val t1: Test1,
        val test2: Test2
    ) {
        fun run() = "${t1.get()} ${test2.get()}"
    }

    private val module1 = module {
        bind<Test1, Test1Impl>()
        provide<Test2, Test2Provider>()
    }

    private val module2 = module {}

    private val module3 = module {
        bindConcrete<Test3>()
        bindConcrete<TestRoot>()
    }

    @Test
    fun main() = runBlockingTest {
        val testProvider = createContextAndGet(
            type<Provider<TestRoot>>(),
            listOf(module1, module2, module3)
        )

        val test = testProvider.getInstance()

        assertEquals(
            "Test1Impl Test2Impl Test3 Test2Impl Test3",
            test.run()
        )
    }
}
