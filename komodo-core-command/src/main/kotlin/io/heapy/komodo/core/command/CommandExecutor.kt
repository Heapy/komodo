package io.heapy.komodo.core.command

/**
 * Finds command by name and executes it.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public interface CommandExecutor {
    public suspend fun execute(name: String)
}
