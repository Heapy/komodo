package io.heapy.komodo.core.command

import io.heapy.komodo.exceptions.KomodoException

/**
 * @author Ruslan Ibragimov
 * @since 1.0
 */
internal class DefaultCommandExecutor(
    private val commands: List<Command>
) : CommandExecutor {
    override suspend fun execute(name: String) {
        val command = commands.find { it.name == name }
            ?: throw CommandNotFoundException(name)

        return command.run()
    }
}

private class CommandNotFoundException(
    name: String
) : KomodoException("Command with name $name not found.", "CORE", "0001")
