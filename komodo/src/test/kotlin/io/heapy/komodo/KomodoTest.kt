package io.heapy.komodo

import io.heapy.komodo.di.module
import io.heapy.komodo.di.provide
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KomodoReturningTest {
    @Test
    fun `test inline module`() = runBlockingTest {
        val result = komodoReturning<Application, String> {
            provide(::Application)
            provide(::Service1)
            provide(::Service2)
        }

        assertEquals("Hello, World!", result)
    }

    @Test
    fun `test module`() = runBlockingTest {
        val result = komodoReturning<Application, String> {
            provide(::Application)
            dependency(k1)
        }

        assertEquals("Hello, World!", result)
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

    private val k1 by module {
        dependency(k2)
        provide(::Service1)
    }

    private val k2 by module {
        provide(::Service2)
    }
}

class KomodoTest {
    @Test
    fun `test inline module`() = runBlockingTest {
        komodo<Application> {
            provide(::Application)
            provide(::Service1)
            provide(::Service2)
        }
    }

    @Test
    fun `test module`() = runBlockingTest {
        komodo<Application> {
            provide(::Application)
            dependency(k1)
        }
    }

    class Application(
        private val service1: Service1
    ) : UnitEntryPoint {
        override suspend fun run() {
            service1.run()
        }
    }

    class Service1(
        private val service2: Service2
    ) {
        fun run() {
            service2.hello()
        }
    }

    class Service2 {
        fun hello(): String {
            return "Hello, World!"
        }
    }

    private val k1 by module {
        dependency(k2)
        provide(::Service1)
    }

    private val k2 by module {
        provide(::Service2)
    }
}
