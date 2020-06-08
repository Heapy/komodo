package io.heapy.komodo.env

/**
 * Interface to access command line arguments in komodo.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public interface KomodoArgs {
    public val args: List<String>
}

/**
 * Default implementation of [KomodoArgs].
 * Which just data class with read-only list of arguments.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
internal data class DefaultKomodoArgs(
    override val args: List<String>
) : KomodoArgs
