package io.heapy.komodo

import io.heapy.komodo.env.DefaultKomodoArgs
import io.heapy.komodo.env.DefaultKomodoEnv
import io.heapy.komodo.env.KomodoArgs
import io.heapy.komodo.env.KomodoEnv

/**
 * Central framework class.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface Komodo<T : Any> {
    /**
     * Application entry point.
     */
    suspend fun run(
        args: Array<String> = arrayOf()
    ): T

    companion object {
        fun <T : Any> new(): Komodo<T> = DefaultKomodo()
    }
}

class DefaultKomodo<T : Any> : Komodo<T> {
    override suspend fun run(args: Array<String>): T {
        val komodoArgs: KomodoArgs = DefaultKomodoArgs(args = args.toList())
        val komodoEnv: KomodoEnv = DefaultKomodoEnv(env = System.getenv().toMap())

        TODO()
    }
}
