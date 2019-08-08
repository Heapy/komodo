package io.heapy.komodo.config

import java.lang.System.getenv
import kotlin.reflect.KClass
import kotlin.reflect.KFunction2
import kotlin.reflect.KProperty

/**
 * @author Ruslan Ibragimov
 */
class EnvConfiguration<out T : Any>(
    private val klass: KClass<*>,
    private val default: T?,
    private val configuration: Configuration
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val name = propertyToConfigName(configuration, property.name)
        println("Config with name $name requested.") // TODO: LOGGER

        val value: String? = configuration.source(name)

        @Suppress("UNCHECKED_CAST")
        return if (default == null) {
            value ?: throw ConfigurationException("Environment variable $name must be set.")
            convert(klass, value) as T
        } else {
            (convertNullable(klass, value) ?: default) as T
        }
    }

    internal fun <T : Any> convert(klass: KClass<T>, value: String): T {
        @Suppress("UNCHECKED_CAST")
        return when (klass) {
            Int::class -> (value.toIntOrNull() ?: throw ConfigurationException("$value can't be parsed as Int.")) as T
            Long::class -> (value.toLongOrNull()
                ?: throw ConfigurationException("$value can't be parsed as Long.")) as T
            String::class -> value as T
            else -> throw ConfigurationException("Unexpected value type.")
        }
    }

    internal fun <T : Any> convertNullable(klass: KClass<T>, value: String?): T? {
        @Suppress("UNCHECKED_CAST")
        return when (klass) {
            Int::class -> value?.toIntOrNull() as T?
            Long::class -> value?.toLongOrNull() as T?
            String::class -> value as T?
            else -> null
        }
    }
}

class ConfigurationException(
    override val message: String
) : RuntimeException()

interface Configuration {
    val app: String
    val source: ConfigurationSource
}

typealias ConfigurationSource = (String) -> String?

val DEFAULT_SOURCE: ConfigurationSource = { getenv(it) }

/**
 * If default not set, delegate will throw exception
 * in case if no value founded in configuration.
 */
inline fun <reified T : Any> Configuration.conf(
    default: T? = null
): EnvConfiguration<T> {
    return EnvConfiguration(T::class, default, this)
}

fun <T : Configuration> Configuration.subConf(
    ctor: KFunction2<String, ConfigurationSource, T>
): ConfigurationWrapper<T> {
    return ConfigurationWrapper(this, ctor)
}

class ConfigurationWrapper<out T : Configuration>(
    private val configuration: Configuration,
    private val ctor: KFunction2<String, ConfigurationSource, T>
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return ctor(propertyToConfigName(configuration, property.name), configuration.source)
    }
}

internal fun propertyToConfigName(
    configuration: Configuration,
    name: String
): String {
    val snake = name.replace(UPPER_LETTER) { "_${it.value}" }
    return "${configuration.app}_$snake".toUpperCase()
}

private val UPPER_LETTER = Regex("[A-Z]")

/**
 * Configuration of application.
 *
 * @author Ruslan Ibragimov
 */
class AppConfiguration(
    override val app: String = "BKUG",
    override val source: ConfigurationSource = DEFAULT_SOURCE
) : Configuration {
    val data by conf("./data")
    val out by conf("./out")
    val env by conf("dev")

    val server by subConf(::ServerConfiguration)
    val database by subConf(::DatabaseConfiguration)
}

class ServerConfiguration(
    override val app: String,
    override val source: ConfigurationSource
) : Configuration {
    val port by conf(8080)
    val host by conf("0.0.0.0")
}

class DatabaseConfiguration(
    override val app: String,
    override val source: ConfigurationSource
) : Configuration {
    val url by conf("jdbc:postgresql://localhost:5436/bkug")
    val username = "bkug"
    val password = "bkug"
    val driverClassName = "org.postgresql.Driver"
    val maximumPoolSize by conf(4)
}
