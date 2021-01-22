package io.heapy.komodo.shutdown

import io.heapy.komodo.logging.logger
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * Implementation based on [Runtime.addShutdownHook].
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
internal class JvmShutdownManager : ShutdownManager {
    private val listeners = CopyOnWriteArraySet<ShutdownListener>()

    private val shutdownListener = thread(start = false) {
        if (_isShuttingDown.getAndSet(true)) {
            LOGGER.info("Shutdown already called, skipping system shutdown callback.")
            return@thread
        }
        LOGGER.info("System shutdown callback called.")
        shutdown()
    }

    fun start() {
        Runtime.getRuntime().addShutdownHook(shutdownListener)
    }

    override fun shutdown(message: String) {
        if (_isShuttingDown.getAndSet(true)) {
            LOGGER.info("Shutdown already called, skipping user shutdown callback.")
            return
        }
        LOGGER.info("Application shutting down: $message")
        Runtime.getRuntime().removeShutdownHook(shutdownListener)
        shutdown()
        exitProcess(0)
    }

    override fun shutdown(throwable: Throwable, exitCode: Int) {
        if (_isShuttingDown.getAndSet(true)) {
            LOGGER.info("Shutdown already called, skipping user shutdown callback.")
            return
        }
        LOGGER.info("Application shutting down exceptionally: {}", throwable.message)
        Runtime.getRuntime().removeShutdownHook(shutdownListener)
        shutdown()
        exitProcess(exitCode)
    }

    private val _isShuttingDown = AtomicBoolean(false)

    override val isShuttingDown = _isShuttingDown.get()

    override fun addShutdownListener(
        name: String,
        priority: Int,
        callback: suspend () -> Unit
    ): RemoveListenerFunction {
        val listener = ShutdownListener(name, priority, callback)
        listeners.add(listener)
        return {
            listeners.remove(listener)
        }
    }

    private fun shutdown() {
        runBlocking {
            listeners.sortedByDescending { it.priority }
                .forEach {
                    try {
                        it.callback()
                    } catch (e: Throwable) {
                        LOGGER.error(
                            "ShutdownListener {} failed with error {}",
                            it.callback::class.java.name,
                            e.message
                        )
                    }
                }
        }
    }

    companion object {
        private val LOGGER = logger<JvmShutdownManager>()
    }
}

private data class ShutdownListener(
    val name: String,
    val priority: Int,
    val callback: suspend () -> Unit
)
