package io.heapy.komodo.core.concurent

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Thread factory which uses [AtomicInteger] to generate thread ids.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
private class DefaultKomodoThreadFactory(
    private val isDaemon: Boolean,
    private val nameProducer: ThreadNameProducer
) : ThreadFactory {
    private val counter = AtomicInteger()

    override fun newThread(runnable: Runnable): Thread {
        val id = counter.incrementAndGet()

        return Thread(runnable).also {
            it.name = nameProducer(id)
            it.isDaemon = isDaemon
        }
    }
}

private typealias ThreadNameProducer = (counter: Int) -> String

/**
 * Create instance of [ThreadFactory] with predefined configuration.
 */
@Suppress("FunctionName")
public fun KomodoThreadFactory(
    isDaemon: Boolean = false,
    nameProducer: ThreadNameProducer = { "komodo-$it" }
): ThreadFactory {
    return DefaultKomodoThreadFactory(isDaemon, nameProducer)
}
