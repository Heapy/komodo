package io.heapy.komodo.shutdown

import kotlinx.coroutines.runBlocking
import java.util.concurrent.CopyOnWriteArraySet
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

    fun start() {
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            runBlocking {
                listeners.sortedByDescending { it.priority }
                    .forEach {
                        try {
                            it.callback()
                        } catch (e: Throwable) {
                            println(e.message)
                        }
                    }
            }
        })
    }

    override fun shutdown(message: String): Nothing {
        println("Application shutting down: $message")
        exitProcess(0)
    }

    override fun shutdown(throwable: Throwable, exitCode: Int): Nothing {
        println("Application shutting down exceptionally: ${throwable.message}")
        exitProcess(exitCode)
    }

    override fun addShutdownListener(name: String, priority: Int, callback: suspend () -> Unit): RemoveListenerFunction {
        val listener = ShutdownListener(name, priority, callback)
        listeners.add(listener)
        return {
            listeners.remove(listener)
        }
    }
}

private data class ShutdownListener(
    val name: String,
    val priority: Int,
    val callback: suspend () -> Unit
)
