package io.heapy.komodo.file

import io.heapy.komodo.exceptions.KomodoException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Provider for resources located in filesystem.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class FileSystemByteStreamProvider(
    private val path: String
) : ByteStreamProvider {
    override fun getByteStream(): InputStream {
        return try {
            Files.newInputStream(Paths.get(path))
        } catch (e: IOException) {
            throw KomodoException("CORE-1")
        }
    }
}
