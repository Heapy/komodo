package io.heapy.komodo.command

/**
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class DefaultCommandExecutor(
    private val commands: List<Command<*>>
) : CommandExecutor {
    override suspend fun <R> execute(name: String): R {
        val command = commands.find { it.name == name }

        return (command ?: NoopCommand).run(CommandArguments()) as R
    }
}

object NoopCommand : Command<Unit> {
    override val name: String = "noop"

    override suspend fun run(arguments: CommandArguments) {
        println("Noop command.")
    }
}
