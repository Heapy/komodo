package io.heapy

import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories

object Extensions {
    fun Project.defaultRepositories() {
        repositories {
            jcenter()
            maven { url = uri("https://dl.bintray.com/heapy/heap/") }
        }
    }
}
