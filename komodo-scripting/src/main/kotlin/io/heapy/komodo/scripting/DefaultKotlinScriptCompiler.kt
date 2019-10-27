package io.heapy.komodo.scripting

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.io.InputStream
import kotlin.coroutines.CoroutineContext

/**
 * Default implementation which used [KotlinJsr223JvmLocalScriptEngineFactory].
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class DefaultKotlinScriptCompiler(
    private val context: CoroutineContext = Dispatchers.IO
) : KotlinScriptCompiler {
    private val factory = KotlinJsr223JvmLocalScriptEngineFactory()
    private val scriptEngine = factory.scriptEngine

    override suspend fun <T> execute(inputStream: InputStream): T {
        return withContext(context = context) {
            @Suppress("UNCHECKED_CAST")
            scriptEngine.eval(inputStream.reader()) as T
        }
    }

    override suspend fun <T> execute(script: String): T {
        return withContext(context = context) {
            @Suppress("UNCHECKED_CAST")
            scriptEngine.eval(script) as T
        }
    }
}
