package io.heapy.komodo.shutdown

/**
 * Shutdown manager used to gracefully shutdown application.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public interface ShutdownManager {
    /**
     * Initiates shutdown.
     */
    public fun shutdown(message: String, exitCode: Int = 0): Nothing

    /**
     * Check current state of manager. Either shutdown called explicitly or system callback called.
     */
    public val isShuttingDown: Boolean

    /**
     * Adds listener to manager. Listener will be called upon shutdown.
     */
    public fun addShutdownListener(
        name: String,
        priority: Int,
        callback: suspend () -> Unit
    ): RemoveListenerFunction
}

/**
 * Removes shutdown listener from the [ShutdownManager].
 * Return [Boolean.true] if listener was removed, [Boolean.false] otherwise.
 */
public typealias RemoveListenerFunction = () -> Boolean
