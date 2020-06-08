package io.heapy.komodo.deferrify

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future

/**
 * @author Ruslan Ibragimov
 * @since 1.0.0
 */
class DeferrifyTest {
    @Test
    fun `future completes successfully`(): Unit = runBlocking {
        val future: Future<String> = CompletableFuture.supplyAsync {
            Thread.sleep(100)
            "Hello!"
        }

        assertEquals("Hello!", future.toDeferred(waitTime = 10).await())
        assertTrue(future.isDone, "Future should be done")
        assertFalse(future.isCancelled, "Future should not be cancelled")
    }

    @Test
    fun `future completes exceptionally`() {
        val future: Future<String> = CompletableFuture.supplyAsync {
            Thread.sleep(100)
            throw RuntimeException("Boom!")
        }

        assertThrows<ExecutionException> {
            runBlocking {
                future.toDeferred(waitTime = 10).await()
            }
        }
        assertTrue(future.isDone, "Future should be done")
        assertFalse(future.isCancelled, "Future should not be cancelled")
    }

    @Test
    fun `future cancelled`() {
        val future: Future<Unit> = CompletableFuture.supplyAsync {
            Thread.sleep(100000)
        }

        future.cancel(false)

        assertThrows<CancellationException> {
            runBlocking {
                future.toDeferred(waitTime = 10).await()
            }
        }
        assertTrue(future.isDone, "Future should be done")
        assertTrue(future.isCancelled, "Future should be cancelled")
    }
}
