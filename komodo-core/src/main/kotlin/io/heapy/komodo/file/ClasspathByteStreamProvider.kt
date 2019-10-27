package io.heapy.komodo.file

import io.heapy.komodo.exceptions.KomodoException
import java.io.InputStream

/**
 * Provider for resources located in classpath.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class ClasspathByteStreamProvider(
    private val path: String
) : ByteStreamProvider {
    override fun getByteStream(): InputStream {
        val stream: InputStream? = ClasspathByteStreamProvider::class.java.classLoader.getResourceAsStream(path)
        return stream ?: throw KomodoException("CORE-1")
    }
}
