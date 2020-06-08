package io.heapy.komodo

/**
 * Represents entry point of application.
 *
 * Usually this method runs something like web server or desktop application.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
interface EntryPoint<out R> {
    suspend fun run(): R
}

typealias UnitEntryPoint = EntryPoint<Unit>
