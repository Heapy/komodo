package io.heapy.komodo.experimental

/**
 * Annotation to mark experimental APIs.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
public annotation class ExperimentalApi
