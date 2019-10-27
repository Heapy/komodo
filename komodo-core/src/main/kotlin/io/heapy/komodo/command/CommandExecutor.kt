package io.heapy.komodo.command

/**
 * Finds command by name and executes it.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface CommandExecutor {
    suspend fun <R> execute(name: String): R
}
