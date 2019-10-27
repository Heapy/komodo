package io.heapy.komodo.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker

/**
 * Function to retrieve logger by class.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
inline fun <reified T : Any> logger(): Logger = LoggerFactory.getLogger(T::class.java)

/* TRACE */

/**
 * Compute and log a message at the TRACE level.
 */
inline fun Logger.trace(msg: () -> String) {
    if (isTraceEnabled) {
        trace(msg())
    }
}

/**
 * Compute and log an exception (throwable) at the TRACE level with an
 * accompanying message.
 */
inline fun Logger.trace(t: Throwable, msg: () -> String) {
    if (isTraceEnabled) {
        trace(msg(), t)
    }
}

/**
 * Compute and log a message with the specific Marker at the TRACE level.
 */
inline fun Logger.trace(marker: Marker, msg: () -> String) {
    if (isTraceEnabled(marker)) {
        trace(marker, msg())
    }
}
/**
 * Compute and log an exception (throwable) at the TRACE level with an
 * accompanying message and specific Marker.
 */
inline fun Logger.trace(marker: Marker, t: Throwable, msg: () -> String) {
    if (isTraceEnabled(marker)) {
        trace(marker, msg(), t)
    }
}

/* DEBUG */

/**
 * Compute and log a message at the DEBUG level.
 */
inline fun Logger.debug(msg: () -> String) {
    if (isDebugEnabled) {
        debug(msg())
    }
}

/**
 * Compute and log an exception (throwable) at the DEBUG level with an
 * accompanying message.
 */
inline fun Logger.debug(t: Throwable, msg: () -> String) {
    if (isDebugEnabled) {
        debug(msg(), t)
    }
}

/**
 * Compute and log a message with the specific Marker at the DEBUG level.
 */
inline fun Logger.debug(marker: Marker, msg: () -> String) {
    if (isDebugEnabled(marker)) {
        debug(marker, msg())
    }
}
/**
 * Compute and log an exception (throwable) at the DEBUG level with an
 * accompanying message and specific Marker.
 */
inline fun Logger.debug(marker: Marker, t: Throwable, msg: () -> String) {
    if (isDebugEnabled(marker)) {
        debug(marker, msg(), t)
    }
}

/* INFO */

/**
 * Compute and log a message at the INFO level.
 */
inline fun Logger.info(msg: () -> String) {
    if (isInfoEnabled) {
        info(msg())
    }
}

/**
 * Compute and log an exception (throwable) at the INFO level with an
 * accompanying message.
 */
inline fun Logger.info(t: Throwable, msg: () -> String) {
    if (isInfoEnabled) {
        info(msg(), t)
    }
}

/**
 * Compute and log a message with the specific Marker at the INFO level.
 */
inline fun Logger.info(marker: Marker, msg: () -> String) {
    if (isInfoEnabled(marker)) {
        info(marker, msg())
    }
}
/**
 * Compute and log an exception (throwable) at the INFO level with an
 * accompanying message and specific Marker.
 */
inline fun Logger.info(marker: Marker, t: Throwable, msg: () -> String) {
    if (isInfoEnabled(marker)) {
        info(marker, msg(), t)
    }
}

/* WARN */

/**
 * Compute and log a message at the WARN level.
 */
inline fun Logger.warn(msg: () -> String) {
    if (isWarnEnabled) {
        warn(msg())
    }
}

/**
 * Compute and log an exception (throwable) at the WARN level with an
 * accompanying message.
 */
inline fun Logger.warn(t: Throwable, msg: () -> String) {
    if (isWarnEnabled) {
        warn(msg(), t)
    }
}

/**
 * Compute and log a message with the specific Marker at the WARN level.
 */
inline fun Logger.warn(marker: Marker, msg: () -> String) {
    if (isWarnEnabled(marker)) {
        warn(marker, msg())
    }
}
/**
 * Compute and log an exception (throwable) at the WARN level with an
 * accompanying message and specific Marker.
 */
inline fun Logger.warn(marker: Marker, t: Throwable, msg: () -> String) {
    if (isWarnEnabled(marker)) {
        warn(marker, msg(), t)
    }
}

/* ERROR */

/**
 * Compute and log a message at the ERROR level.
 */
inline fun Logger.error(msg: () -> String) {
    if (isErrorEnabled) {
        error(msg())
    }
}

/**
 * Compute and log an exception (throwable) at the ERROR level with an
 * accompanying message.
 */
inline fun Logger.error(t: Throwable, msg: () -> String) {
    if (isErrorEnabled) {
        error(msg(), t)
    }
}

/**
 * Compute and log a message with the specific Marker at the ERROR level.
 */
inline fun Logger.error(marker: Marker, msg: () -> String) {
    if (isErrorEnabled(marker)) {
        error(marker, msg())
    }
}
/**
 * Compute and log an exception (throwable) at the ERROR level with an
 * accompanying message and specific Marker.
 */
inline fun Logger.error(marker: Marker, t: Throwable, msg: () -> String) {
    if (isErrorEnabled(marker)) {
        error(marker, msg(), t)
    }
}
