package io.heapy.komodo.shutdown

/**
 * Shutdown manager used to gracefully shutdown application.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface ShutdownManager {
    /**
     * Initiates shutdown, because application completes invocation successfully.
     */
    fun shutdown(message: String): Nothing

    /**
     * Initiates shutdown, because some exception that can't be recovered.
     */
    fun shutdown(throwable: Throwable, exitCode: Int = 1): Nothing

    /**
     * Adds listener to manager. Listener will be called upon shutdown.
     */
    fun addShutdownListener(
        name: String,
        priority: Int,
        callback: suspend () -> Unit
    ): RemoveListenerFunction
}

data class ShutdownListener(
    val name: String,
    val priority: Int,
    val callback: suspend () -> Unit
)

/**
 * Removes shutdown listener from the [ShutdownManager].
 * Return [Boolean.true] if listener was registered, [Boolean.false] otherwise.
 */
typealias RemoveListenerFunction = () -> Boolean
