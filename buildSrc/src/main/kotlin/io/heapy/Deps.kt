package io.heapy

import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import java.util.*

object Libs {
    val kotlinVersion = kotlinPluginVersion().also { println("Kotlin Version: $it") }
    val kotlinStdlib = Lib("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", kotlinVersion)
    val kotlinReflect = Lib("org.jetbrains.kotlin", "kotlin-reflect", kotlinVersion)
    val kotlinScriptUtil = Lib("org.jetbrains.kotlin", "kotlin-script-util", kotlinVersion)
    val kotlinCompilerEmbeddable = Lib("org.jetbrains.kotlin", "kotlin-compiler-embeddable", kotlinVersion)

    const val kotlinxCoroutinesVersion = "1.3.7"
    val kotlinxCoroutines = Lib("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", kotlinxCoroutinesVersion)
    val kotlinxCoroutinesTest = Lib("org.jetbrains.kotlinx", "kotlinx-coroutines-test", kotlinxCoroutinesVersion)

    const val slf4jVersion = "2.0.0-alpha1"
    val slf4jApi = Lib("org.slf4j", "slf4j-api", slf4jVersion)
    val slf4jSimple = Lib("org.slf4j", "slf4j-simple", slf4jVersion)

    const val logbackVersion = "1.3.0-alpha5"
    val logbackClassic = Lib("ch.qos.logback", "logback-classic", logbackVersion)

    const val junitVersion = "5.6.2"
    val jupiterApi = Lib("org.junit.jupiter", "junit-jupiter-api", junitVersion)
    val jupiterEngine = Lib("org.junit.jupiter", "junit-jupiter-engine", junitVersion)

    const val junitPlatformVersion = "1.6.2"
    val junitPlatformLauncher = Lib("org.junit.platform", "junit-platform-launcher", junitPlatformVersion)

    const val mockkVersion = "1.10.0"
    val mockk = Lib("io.mockk", "mockk", mockkVersion)

    const val undertowVersion = "2.1.3.Final"
    val undertow = Lib("io.undertow", "undertow-core", undertowVersion)

    const val apacheHttpClientVersion = "4.1.4"
    val httpasyncclient = Lib("org.apache.httpcomponents", "httpasyncclient", apacheHttpClientVersion)

    const val hikariCPVersion = "3.4.5"
    val hikariCP = Lib("com.zaxxer", "HikariCP", hikariCPVersion)

    const val ktorVersion = "1.3.1"
    val ktorClientApache = Lib("io.ktor", "ktor-client-apache", ktorVersion)
    val ktorClientJackson = Lib("io.ktor", "ktor-client-jackson", ktorVersion)

    const val jacksonVersion = "2.11.0"
    val jacksonKotlin = Lib("com.fasterxml.jackson.module", "jackson-module-kotlin", jacksonVersion)
    val jacksonXml = Lib("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", jacksonVersion)

    val libraries: List<Lib>
        get() = listOf(
            kotlinStdlib,
            kotlinReflect,
            kotlinScriptUtil,
            kotlinCompilerEmbeddable,
            kotlinxCoroutines,
            kotlinxCoroutinesTest,
            slf4jApi,
            slf4jSimple,
            logbackClassic,
            jupiterApi,
            jupiterEngine,
            junitPlatformLauncher,
            mockk,
            undertow,
            httpasyncclient,
            hikariCP,
            ktorClientApache,
            ktorClientJackson,
            jacksonKotlin,
            jacksonXml
        )

    /**
     * Get all known komodo libraries
     */
    fun getKomodoLibraries(komodoVersion: String): List<Lib> {
        fun komodoLib(
            group: String,
            artifact: String,
            publish: Boolean
        ): Lib {
            return Lib(
                group = group,
                artifact = artifact,
                version = komodoVersion,
                publish = publish
            )
        }

        return listOf(
            komodoLib("io.heapy.komodo", "komodo", true),
            komodoLib("io.heapy.komodo", "komodo-config", true),
            komodoLib("io.heapy.komodo", "komodo-config-dotenv", true),
            komodoLib("io.heapy.komodo", "komodo-core-beans", true),
            komodoLib("io.heapy.komodo", "komodo-core-cli", true),
            komodoLib("io.heapy.komodo", "komodo-core-command", true),
            komodoLib("io.heapy.komodo", "komodo-core-concurrent", true),
            komodoLib("io.heapy.komodo", "komodo-core-coroutines", true),
            komodoLib("io.heapy.komodo", "komodo-di", true),
            komodoLib("io.heapy.komodo.utils", "komodo-deferrify", true),
            komodoLib("io.heapy.komodo", "komodo-root", false),
            komodoLib("io.heapy.komodo", "komodo-bom", false),
            komodoLib("io.heapy.komodo", "komodo-docs", false),
            komodoLib("io.heapy.komodo", "komodo-logging", false)
        )
    }
}

data class Lib(
    val group: String,
    val artifact: String,
    val version: String,
    val publish: Boolean = true
) {
    fun dep(): String = "$group:$artifact:$version"
}

private fun kotlinPluginVersion(): String {
    return KotlinPluginWrapper::class.java.classLoader
        .getResource("project.properties")!!
        .openStream().use { propsStream ->
            Properties().let {
                it.load(propsStream)
                it.getProperty("project.version")
            }
        }
}
