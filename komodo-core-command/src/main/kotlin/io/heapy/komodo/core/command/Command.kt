package io.heapy.komodo.core.command

/**
 * Base command interface
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public interface Command {
    /**
     * Probably should be replaced with object from komodo-core-cli
     * TODO!
     */
    public val name: String
    public suspend fun run()
}
