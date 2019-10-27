package io.heapy

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.repositories
import java.net.URI

object Extensions {
    fun RepositoryHandler.kotlinRepositories() {
        when {
            Libs.kotlinVersion.contains("dev") -> maven { url = URI("https://dl.bintray.com/kotlin/kotlin-dev/") }
            Libs.kotlinVersion.contains("eap") -> maven { url = URI("https://dl.bintray.com/kotlin/kotlin-eap/") }
        }
    }

    fun Project.defaultRepositories() {
        repositories {
            jcenter()
            kotlinRepositories()
            maven { url = uri("https://dl.bintray.com/heapy/heap/") }
        }
    }
}
