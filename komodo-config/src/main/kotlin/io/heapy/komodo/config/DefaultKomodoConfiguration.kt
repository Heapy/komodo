package io.heapy.komodo.config

import io.heapy.komodo.exceptions.KomodoException
import io.heapy.komodo.scripting.KotlinScriptCompiler
import kotlinx.coroutines.sync.Mutex
import kotlin.reflect.KClass

/**
 * Default implementation if komodo configuration.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */

class KotlinScriptKomodoConfiguration(
    private val kotlinConfigurationSources: KomodoConfigurationSources,
    private val kotlinScriptCompiler: KotlinScriptCompiler
) : KomodoConfiguration {
    private var configs: List<Any> = listOf()
    private val mutex = Mutex()

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> getConfig(klass: KClass<T>): T? {
        // TODO: Meh, looks ugly, any better alternatives?
        if (configs.isEmpty()) {
            mutex.lock()
            try {
                if (configs.isEmpty())
                    configs = createConfig()
            } finally {
                mutex.unlock()
            }
        }

        return try {
            configs.find { klass.isInstance(it) } as T?
        } catch (e: ClassCastException) {
            throw KomodoException("CORE-2")
        }
    }

    private suspend fun createConfig(): List<Any> {
        return kotlinConfigurationSources.getSources().map { sourceProvider ->
            val result = kotlinScriptCompiler.execute<Any>(sourceProvider.getInputStream())

            when (result) {
                is List<*> -> result.filterNotNull()
                else -> listOf(result)
            }
        }.flatten()
    }

}






