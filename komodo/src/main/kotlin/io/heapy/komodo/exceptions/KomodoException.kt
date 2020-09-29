package io.heapy.komodo.exceptions

/**
 * Base class for library exceptions with code for more readable errors.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public open class KomodoException(
    override val message: String,
    private val module: String,
    private val code: String,
    override val cause: Throwable? = null
) : RuntimeException() {
    override fun toString(): String {
        return "KOMODO-$module-$code: $message"
    }
}
