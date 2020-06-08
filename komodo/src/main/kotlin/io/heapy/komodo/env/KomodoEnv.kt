package io.heapy.komodo.env

/**
 * Interface to access environment variables in komodo.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public interface KomodoEnv {
    public val env: Map<String, String>
}

/**
 * Default implementation of [KomodoEnv].
 * Which just data class with read-only map of environment variables.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
internal data class DefaultKomodoEnv(
    override val env: Map<String, String>
) : KomodoEnv
