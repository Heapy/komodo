package io.heapy.komodo.command

/**
 * Base command interface
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface Command<out R> {
    val name: String
    suspend fun run(arguments: CommandArguments): R
}

class CommandArguments
