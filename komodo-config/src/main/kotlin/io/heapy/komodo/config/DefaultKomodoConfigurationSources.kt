package io.heapy.komodo.config

import io.heapy.komodo.file.InputStreamProvider
import io.heapy.komodo.file.getInputStreamProvider

class DefaultKomodoConfigurationSources(
    private val args: io.heapy.komodo.env.KomodoArgs
    // private val cmdParser: Parser// or KmdOptions
) : io.heapy.komodo.config.KomodoConfigurationSources {
    override fun getSources(): List<InputStreamProvider> {
        // TODO: Parse args and provide real config
        return listOf(getInputStreamProvider("classpath:config.kts"))
    }
}
