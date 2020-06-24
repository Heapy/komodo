package io.heapy.komodo

/**
 * Represents entry point of application.
 *
 * Usually this method runs something like web server or desktop application.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public interface EntryPoint<out R> {
    public suspend fun run(): R
}

/**
 * Entry point which doesn't return any values.
 * Useful for webservers.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
public typealias UnitEntryPoint = EntryPoint<Unit>
