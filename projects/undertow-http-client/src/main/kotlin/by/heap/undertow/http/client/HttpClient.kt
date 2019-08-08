package by.heap.undertow.http.client

/**
 * TODO.
 *
 * @author Ibragimov Ruslan
 * @since 0.0.5
 */
interface HttpClient {
    suspend fun get(url: String): String
}