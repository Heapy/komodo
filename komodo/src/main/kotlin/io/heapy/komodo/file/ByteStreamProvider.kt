package io.heapy.komodo.file

import io.heapy.komodo.exceptions.KomodoException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

private const val CLASSPATH_PREFIX = "classpath:"

/**
 * Interface for classes which can provide input stream
 * of their content.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public interface ByteStreamProvider {
    /**
     * Returns new byte stream for each call or null,
     * if steam could not be open.
     *
     * TODO: Replace with suspend alternative
     */
    public fun getByteStream(): InputStream
}

/**
 * Get input stream provider by path
 */
public fun getInputStreamProvider(path: String): ByteStreamProvider = when {
    path.startsWith(CLASSPATH_PREFIX) -> ClasspathByteStreamProvider(path.removePrefix(CLASSPATH_PREFIX))
    else -> FileSystemByteStreamProvider(path)
}

/**
 * Provider for resources located in classpath.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
private class ClasspathByteStreamProvider(
    private val path: String
) : ByteStreamProvider {
    override fun getByteStream(): InputStream {
        val stream: InputStream? = ClasspathByteStreamProvider::class.java.classLoader.getResourceAsStream(path)
        return stream ?: throw KomodoException("Classpath resource for $path not found", "CORE", "0002")
    }
}

/**
 * Provider for resources located in filesystem.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
private class FileSystemByteStreamProvider(
    private val path: String
) : ByteStreamProvider {
    override fun getByteStream(): InputStream {
        return try {
            Files.newInputStream(Paths.get(path))
        } catch (e: IOException) {
            throw KomodoException("Filesystem resource for $path not found", "CORE", "0003", e)
        }
    }
}
