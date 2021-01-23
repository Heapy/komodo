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
public class JvmShutdownManager : ShutdownManager {
    private val listeners = CopyOnWriteArraySet<ShutdownListener>()

    public fun start() {
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            if (_isShuttingDown.getAndSet(true)) {
                LOGGER.info("Shutdown already called, skipping system shutdown callback.")
                return@thread
            }
            LOGGER.info("System shutdown callback called.")
            shutdown()
        })
    }

    override fun shutdown(message: String, exitCode: Int): Nothing {
        if (_isShuttingDown.getAndSet(true)) {
            LOGGER.info("Shutdown already called, skipping user shutdown request.")
            exitProcess(exitCode)
        } else {
            LOGGER.info("Application shutting down: $message")
            shutdown()
            exitProcess(exitCode)
        }
    }

    private val _isShuttingDown = AtomicBoolean(false)

    override val isShuttingDown: Boolean = _isShuttingDown.get()

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

    private companion object {
        private val LOGGER = logger<JvmShutdownManager>()
    }
}

private data class ShutdownListener(
    val name: String,
    val priority: Int,
    val callback: suspend () -> Unit
)
