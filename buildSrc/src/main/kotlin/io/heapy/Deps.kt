package io.heapy

object Libs {
    const val kotlinVersion = "1.3.61"
    val kotlinStdlib = Lib("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", kotlinVersion)
    val kotlinReflect = Lib("org.jetbrains.kotlin", "kotlin-reflect", kotlinVersion)
    val kotlinScriptUtil = Lib("org.jetbrains.kotlin", "kotlin-script-util", kotlinVersion)
    val kotlinCompilerEmbeddable = Lib("org.jetbrains.kotlin", "kotlin-compiler-embeddable", kotlinVersion)

    const val kotlinxCoroutinesVersion = "1.3.3"
    val kotlinxCoroutines = Lib("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", kotlinxCoroutinesVersion)

    const val slf4jVersion = "2.0.0-alpha1"
    val slf4jApi = Lib("org.slf4j", "slf4j-api", slf4jVersion)
    val slf4jSimple = Lib("org.slf4j", "slf4j-simple", slf4jVersion)

    const val logbackVersion = "1.3.0-alpha5"
    val logbackClassic = Lib("ch.qos.logback", "logback-classic", logbackVersion)

    const val junitVersion = "5.6.0"
    val jupiterApi = Lib("org.junit.jupiter", "junit-jupiter-api", junitVersion)
    val jupiterEngine = Lib("org.junit.jupiter", "junit-jupiter-engine", junitVersion)

    const val junitPlatformVersion = "1.6.0"
    val junitPlatformLauncher = Lib("org.junit.platform", "junit-platform-launcher", junitPlatformVersion)

    const val mockkVersion = "1.9.3"
    val mockk = Lib("io.mockk", "mockk", mockkVersion)

    const val undertowVersion = "2.0.29.Final"
    val undertow = Lib("io.undertow", "undertow-core", undertowVersion)

    const val apacheHttpClientVersion = "4.1.4"
    val httpasyncclient = Lib("org.apache.httpcomponents", "httpasyncclient", apacheHttpClientVersion)

    const val hikariCPVersion = "3.4.2"
    val hikariCP = Lib("com.zaxxer", "HikariCP", hikariCPVersion)

    const val ktorVersion = "1.3.1"
    val ktorClientApache = Lib("io.ktor", "ktor-client-apache", ktorVersion)
    val ktorClientJackson = Lib("io.ktor", "ktor-client-jackson", ktorVersion)

    const val jacksonVersion = "2.10.2"
    val jacksonKotlin = Lib("com.fasterxml.jackson.module", "jackson-module-kotlin", jacksonVersion)
    val jacksonXml = Lib("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", jacksonVersion)

    val ALL: List<Lib> = listOf(
        kotlinStdlib,
        kotlinReflect,
        kotlinScriptUtil,
        kotlinCompilerEmbeddable,
        kotlinxCoroutines,
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
}

data class Lib(
    val group: String,
    val artifact: String,
    val version: String
) {
    fun dep(): String = "$group:$artifact:$version"
}
