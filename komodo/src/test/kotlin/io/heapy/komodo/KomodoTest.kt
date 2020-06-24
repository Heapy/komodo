package io.heapy.komodo

import io.heapy.komodo.di.bindConcrete
import io.heapy.komodo.di.module
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class KomodoTest {
    @Test
    fun `test inline module`() = runBlockingTest {
        val result = komodoReturning<Application, String> {
            module {
                bindConcrete<Service1>()
                bindConcrete<Service2>()
            }
        }

        assertEquals("Hello, World!", result)
    }

    @Test
    fun `test module`() = runBlockingTest {
        val result = komodoReturning<Application, String> {
            module(k1)
        }

        assertEquals("Hello, World!", result)
    }

    @Test
    @Disabled
    fun `test private module`() = runBlockingTest {
        val result = komodoReturning<PrivateApplication, String> {
            module(k1)
        }

        assertEquals("Hello, World!", result)
    }

    private class PrivateApplication(
        private val service1: Service1
    ) : EntryPoint<String> {
        override suspend fun run(): String {
            return service1.run()
        }
    }

    class Application(
        private val service1: Service1
    ) : EntryPoint<String> {
        override suspend fun run(): String {
            return service1.run()
        }
    }

    class Service1(
        private val service2: Service2
    ) {
        fun run(): String {
            return service2.hello()
        }
    }

    class Service2 {
        fun hello(): String {
            return "Hello, World!"
        }
    }

    private val k1 = module {
        dependency(k2)
        bindConcrete<Service1>()
    }

    private val k2 = module {
        bindConcrete<Service2>()
    }
}
