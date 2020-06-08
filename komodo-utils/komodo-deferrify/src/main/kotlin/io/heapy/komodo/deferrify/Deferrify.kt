package io.heapy.komodo.deferrify

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.Future

/**
 * Extension that converts [Future] to [Deferred]
 * without blocking any thread, by using configurable polling.
 *
 * @author Ruslan Ibragimov
 * @since 1.0.0
 */
public fun <T : Any> Future<T>.toDeferred(
    scope: CoroutineScope = GlobalScope,
    waitTime: Long = 5
): Deferred<T> {
    val future = this
    val deferred = CompletableDeferred<T>()
    var isDone = false

    scope.launch {
        while (isActive && !isDone) {
            when {
                // First check isCancelled, since cancelled future will return true for isDone
                future.isCancelled -> {
                    isDone = true
                    deferred.cancel()
                }
                future.isDone -> {
                    isDone = true
                    try {
                        // Get happens after isDone check
                        @Suppress("BlockingMethodInNonBlockingContext")
                        deferred.complete(future.get())
                    } catch (e: Exception) {
                        deferred.completeExceptionally(e)
                    }
                }
                else -> {
                    delay(waitTime)
                }
            }
        }
    }

    return deferred
}
