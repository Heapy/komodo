package io.heapy.komodo.completability

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.InvocationInterceptor
import org.junit.jupiter.api.extension.ReflectiveInvocationContext
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future

/**
 * @author Ruslan Ibragimov
 * @since 0.0.2
 */
class Completability {
    @Test
    fun `future completes successfully`(): Unit = runBlocking {
        val future: Future<String> = CompletableFuture.supplyAsync {
            Thread.sleep(100)
            "Hello!"
        }

        assertEquals("Hello!", future.toDeferred(waitTime = 10).await())
    }

    @Test
    fun `future completes exceptionally`() {
        val future: Future<String> = CompletableFuture.supplyAsync {
            Thread.sleep(100)
            throw RuntimeException("Boom!")
        }

        // TODO:
        assertThrows<ExecutionException> {
            runBlocking {
                future.toDeferred(waitTime = 10).await()
            }
        }
    }
}


@ExtendWith(CoroutinesInterceptor::class)
class Sample {
    @Test
    fun test() {
        assertEquals(1, 0)
    }
}

class CoroutinesInterceptor : InvocationInterceptor {
    override fun interceptTestMethod(
        invocation: InvocationInterceptor.Invocation<Void>,
        invocationContext: ReflectiveInvocationContext<Method>,
        extensionContext: ExtensionContext
    ) {
        invocation.proceed()
    }
}

