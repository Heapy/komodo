package io.heapy.komodo.core.command

import io.heapy.komodo.exceptions.KomodoException
import io.heapy.komodo.shutdown.ShutdownManager

/**
 * @author Ruslan Ibragimov
 * @since 1.0
 */
internal class DefaultCommandExecutor(
    private val commands: List<Command>,
    private val shutdownManager: ShutdownManager
) : CommandExecutor {
    override suspend fun execute(name: String) {
        val command = commands.find { it.name == name }
            ?: shutdownManager.shutdown(CommandNotFoundException(name))

        return command.run()
    }
}

private class CommandNotFoundException(
    name: String
) : KomodoException("Command with name $name not found.", "CORE", "0001")
