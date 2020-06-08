package io.heapy.komodo.config

import io.heapy.komodo.file.ByteStreamProvider

/**
 * Provides configuration data.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public interface KomodoConfigurationSources {
    public fun getSources(): List<ByteStreamProvider>
}
