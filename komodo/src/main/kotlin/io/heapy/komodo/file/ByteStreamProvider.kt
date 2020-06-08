package io.heapy.komodo.file

import java.io.InputStream

private const val CLASSPATH_PREFIX = "classpath:"

/**
 * Interface for classes which can provide input stream
 * of their content.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface ByteStreamProvider {
    /**
     * Returns new byte stream for each call or null,
     * if steam could not be open.
     *
     * TODO: Replace with suspend alternative
     */
    fun getByteStream(): InputStream
}

/**
 * Get input stream provider by path
 */
fun getInputStreamProvider(path: String): ByteStreamProvider = when {
    path.startsWith(CLASSPATH_PREFIX) -> ClasspathByteStreamProvider(path.removePrefix(CLASSPATH_PREFIX))
    else -> FileSystemByteStreamProvider(path)
}
