package io.heapy.komodo

import kotlin.reflect.KClass
import io.heapy.komodo.di.Module

interface Komodo {
    fun args(args: Array<String>): Komodo
    fun env(env: Map<String, String>): Komodo
    fun module(module: Module): Komodo
    suspend fun <R> run(entryPoint: KClass<out EntryPoint<R>>): R
}
