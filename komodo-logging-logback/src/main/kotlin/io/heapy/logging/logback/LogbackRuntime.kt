package io.heapy.logging.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.util.StatusPrinter
import org.slf4j.LoggerFactory


/**
 * TODO.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
fun main() {
    LoggerFactory.getLogger("Hello").error("Hello")

    val rootLogger: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    val lc: LoggerContext = rootLogger.getLoggerContext()
    lc.reset() //  reset prev config


    val encoder = PatternLayoutEncoder()
    encoder.context = lc
    encoder.pattern = "%-5level %message%n"
    encoder.start()

    val appender: ConsoleAppender<ILoggingEvent?> = ConsoleAppender<ILoggingEvent?>()
    appender.context = lc
    appender.encoder = encoder
    appender.start()

    rootLogger.level = Level.DEBUG
    rootLogger.addAppender(appender)

    LoggerFactory.getLogger("Hello").error("Hello")
}

fun configureLogback() {
    val lc: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    lc.reset() //  reset prev config

    val configurator = JoranConfigurator()
    configurator.setContext(lc)
    configurator.doConfigure("config.xml") // any data source

    StatusPrinter.printInCaseOfErrorsOrWarnings(lc)

    // а теперь комбинация:


    val root: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger

    root.level = Level.INFO // переопределение
}
