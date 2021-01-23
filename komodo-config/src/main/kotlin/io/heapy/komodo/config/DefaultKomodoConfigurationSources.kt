package io.heapy.komodo.config

import io.heapy.komodo.env.KomodoArgs
import io.heapy.komodo.file.ByteStreamProvider
import io.heapy.komodo.file.getInputStreamProvider

private class DefaultKomodoConfigurationSources(
    private val args: KomodoArgs
    // private val cmdParser: Parser// or KmdOptions
) : KomodoConfigurationSources {
    override fun getSources(): List<ByteStreamProvider> {
        // TODO: Parse args and provide real config
        return listOf(getInputStreamProvider("classpath:config.kts"))
    }
}
