package io.heapy.komodo.config.dotenv

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Wrapper for [System.getenv], for easy testing and extensibility.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public interface Env {
    /**
     * Throws exception, if env not found
     */
    public fun get(env: String): String {
        return getOrNull(env)
            ?: throw EnvNotDefinedException("$env not defined.")
    }

    /**
     * Returns null, if env not found
     */
    public fun getOrNull(env: String): String?
}

internal class EnvNotDefinedException(message: String) : RuntimeException(message)

public class Dotenv(
    /**
     * Env file location
     */
    private val file: Path = Paths.get(".env"),

    /**
     * Env variable that overrides env file location
     */
    private val fileEnv: String = "KOMODO_DOTENV_FILE",

    /**
     * Ignore file if it doesn't exists
     */
    private val ignoreIfMissing: Boolean = true,

    /**
     * System env variables
     */
    private val system: Map<String, String> = System.getenv()
) : Env {
    private val vars = getEnvironmentVariables()

    override fun getOrNull(env: String): String? {
        return vars[env]
    }

    internal fun getEnvironmentVariables(): Map<String, String> {
        val resolvedFile = system[fileEnv]?.let { Paths.get(it) } ?: file

        return if (Files.exists(resolvedFile)) {
            Files.readAllLines(resolvedFile)
                .filterNot { it.startsWith("#") }
                .filterNot { it.isNullOrEmpty() }
                .map {
                    val (name, value) = it.split("=", limit = 2)
                    name to value
                }
                .toMap()
                .plus(system)
        } else {
            if (ignoreIfMissing) {
                mapOf<String, String>().plus(system)
            } else {
                throw DotenvFileNotFoundException("File ${resolvedFile.toAbsolutePath()} not exists")
            }
        }
    }
}

internal class DotenvFileNotFoundException(message: String) : RuntimeException(message)
