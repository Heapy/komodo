package io.heapy.komodo.env

/**
 * Interface to access environment in komodo.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface KomodoEnv {
    val env: Map<String, String>
}

/**
 * Default implementation of [KomodoEnv].
 * Which just data class with read-only map of environment variables.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
data class DefaultKomodoEnv(
    override val env: Map<String, String>
) : KomodoEnv
