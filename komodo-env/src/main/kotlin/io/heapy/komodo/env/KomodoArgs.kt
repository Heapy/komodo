package io.heapy.komodo.env

/**
 * Interface to access command like variables in komodo.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface KomodoArgs {
    val args: List<String>
}

/**
 * Default implementation of [KomodoArgs].
 * Which just data class with read-only list of arguments.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
data class DefaultKomodoArgs(
    override val args: List<String>
) : KomodoArgs
