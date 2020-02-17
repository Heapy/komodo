package io.heapy.build.jvm

import io.heapy.Libs.junitPlatformLauncher
import io.heapy.Libs.jupiterApi
import io.heapy.Libs.jupiterEngine
import io.heapy.Libs.kotlinStdlib
import io.heapy.Libs.kotlinxCoroutines
import io.heapy.Libs.mockk
import io.heapy.Extensions.defaultRepositories
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Plugin which setups defaults in jvm based komodo modules
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class KomodoJvmPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.defaultRepositories()
        project.coverage()
        project.kotlin()
        project.test()
        project.config()
    }

    private fun Project.kotlin() {
        plugins.apply("org.jetbrains.kotlin.jvm")

        dependencies {
            add("implementation", kotlinStdlib.dep())
            add("implementation", kotlinxCoroutines.dep())
        }
    }

    private fun Project.config() {
        val bytecodeVersion = JavaVersion.VERSION_1_8

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-progressive",
                    "-Xuse-experimental=kotlin.Experimental",
                    "-Xuse-experimental=kotlin.ExperimentalStdlibApi"
                )
                jvmTarget = bytecodeVersion.toString()
            }
        }

        extensions.getByType(JavaPluginExtension::class.java).apply {
            sourceCompatibility = bytecodeVersion
            targetCompatibility = bytecodeVersion
        }
    }

    private fun Project.test() {
        tasks.withType<Test> {
            useJUnitPlatform()

            extensions.getByType(JacocoTaskExtension::class.java).apply {
                destinationFile = file("$buildDir/jacoco/module.exec")
            }
        }

        dependencies {
            add("testImplementation", mockk.dep())
            add("testImplementation", jupiterApi.dep())
            add("testRuntimeOnly", jupiterEngine.dep())
            add("testRuntimeOnly", junitPlatformLauncher.dep())
        }
    }

    private fun Project.coverage() {
        plugins.apply("jacoco")
    }
}
