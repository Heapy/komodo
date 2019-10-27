package io.heapy.komodo.http

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.nio.client.HttpAsyncClient

/**
 * Coroutines adapter for [HttpAsyncClient.execute].
 */
suspend fun HttpAsyncClient.execute(request: HttpUriRequest): HttpResponse {
    return suspendCancellableCoroutine { cont: CancellableContinuation<HttpResponse> ->
        val callback = object : FutureCallback<HttpResponse> {
            override fun completed(result: HttpResponse) {
                println("completed ${request.uri}")
                cont.resumeWith(Result.success(result))
            }

            override fun cancelled() {
                println("cancelled ${request.uri}")
                if (cont.isCancelled) {
                    println("coroutine cancelled ${request.uri}")
                    return
                }
                cont.resumeWith(Result.failure(FutureCallbackCanceledException(request)))
            }

            override fun failed(ex: Exception) {
                println("Failed ${request.uri}")
                cont.resumeWith(Result.failure(ex))
            }
        }

        val future = this.execute(request, callback)

        GlobalScope.launch {
            println("cancel launch ${request.uri}")
            future.cancel(false)
        }

        cont.invokeOnCancellation {
            println("cancel future ${request.uri}")
            future.cancel(false)
        }
        Unit
    }
}

@InternalCoroutinesApi
suspend fun main(args: Array<String>) {
    val asyncClient = HttpAsyncClients.custom()
        .setMaxConnPerRoute(1000)
        .setMaxConnTotal(1000)
        .build()

    try {
        asyncClient.start()


        withTimeoutOrNull(90) {
            val list = mutableListOf<Job>()
            (1..10).forEach {

                list += launch {
                    println(">> Making request")
                    println(asyncClient.execute(HttpGet("https://heapy.io/?q=$it")).body().length)
                    println(">> Request done")
                }
            }

            list.forEach { it.join() }

            list.forEach {
                if (it.isCancelled) {
                    println(it.getCancellationException().message)
                }
            }
        }

    } finally {
        println(">> Finally")
        println(">> Close")
        asyncClient.close()
        println(">> Closed")
    }

}

class FutureCallbackCanceledException(
    private val request: HttpUriRequest
) : CancellationException() {
    override val message: String
        get() = "${request.method} request to ${request.uri} is cancelled."
}

/**
 * Helper method for reading response body.
 */
fun HttpResponse.body(): String {
    return entity.content.bufferedReader(Charsets.UTF_8).use { it.readText() }
}

