package io.heapy.komodo.exceptions

/**
 * Base class for library exceptions with code for more readable errors.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class KomodoException(
    private val code: String
) : RuntimeException() {
    override fun toString(): String {
        return "KomodoException(code='$code')"
    }
}
