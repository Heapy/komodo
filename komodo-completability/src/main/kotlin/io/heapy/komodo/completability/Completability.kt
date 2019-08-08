package io.heapy.komodo.completability

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
 * @since 0.0.2
 */
fun <T : Any> Future<T>.toDeferred(
    scope: CoroutineScope = GlobalScope,
    waitTime: Long = 100
): Deferred<T> {
    val future = this
    val deferred = CompletableDeferred<T>()
    var isDone = false

    scope.launch {
        while (isActive && !isDone) {
            if (future.isDone) {
                try {
                    isDone = true
                    deferred.complete(future.get())
                } catch (e: Exception) {
                    deferred.completeExceptionally(e)
                }
            } else if (future.isCancelled) {
                isDone = true
                deferred.cancel()
            } else {
                delay(waitTime)
            }
        }
    }

    return deferred
}
