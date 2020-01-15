package io.heapy.komodo.command

/**
 * Base command interface
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface Command {
    val name: String
    suspend fun run()
}
