package io.heapy

import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories

object Extensions {
    fun Project.defaultRepositories() {
        repositories {
            mavenCentral()
            maven { url = uri("https://repo.kotlin.link") }
        }
    }
}
