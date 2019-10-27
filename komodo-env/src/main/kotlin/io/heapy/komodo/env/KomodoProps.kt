package io.heapy.komodo.env

/**
 * Interface to access jvm properties in komodo.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface KomodoProps {
    val env: Map<String, String>
}

/**
 * Default implementation of [KomodoProps].
 * Which just data class with read-only map of properties variables.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
data class DefaultKomodoProps(
    override val env: Map<String, String>
) : KomodoEnv
