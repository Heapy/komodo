package io.heapy.build.jvm

import io.heapy.Libs.junitPlatformLauncher
import io.heapy.Libs.jupiterApi
import io.heapy.Libs.jupiterEngine
import io.heapy.Libs.kotlinStdlib
import io.heapy.Libs.kotlinxCoroutines
import io.heapy.Libs.kotlinxCoroutinesTest
import io.heapy.Libs.mockk
import io.heapy.Extensions.defaultRepositories
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Plugin which setups defaults in jvm based komodo modules
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class KomodoJvmPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        defaultRepositories()
        coverage()
        kotlin()
        test()
        config()
    }

    private fun Project.kotlin() {
        pluginManager.apply(KotlinPluginWrapper::class)

        dependencies {
            add("implementation", kotlinStdlib.dep())
            add("implementation", kotlinxCoroutines.dep())
        }
    }

    private fun Project.config() {
        val bytecodeVersion = JavaVersion.VERSION_1_8

        val commonCompilerArgs = listOf(
            "-progressive",
            "-Xopt-in=kotlin.ExperimentalStdlibApi",
            "-Xopt-in=kotlin.RequiresOptIn"
        )

        tasks.named<KotlinCompile>("compileTestKotlin") {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + commonCompilerArgs + listOf(
                    "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
                )
                jvmTarget = bytecodeVersion.toString()
            }
        }

        tasks.named<KotlinCompile>("compileKotlin") {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + commonCompilerArgs + listOf(
                    "-Xexplicit-api=warning"
                )
                jvmTarget = bytecodeVersion.toString()
            }
        }

        extensions.getByType<JavaPluginExtension>().apply {
            sourceCompatibility = bytecodeVersion
            targetCompatibility = bytecodeVersion
        }
    }

    private fun Project.test() {
        tasks.withType<Test> {
            useJUnitPlatform()

            extensions.getByType<JacocoTaskExtension>().apply {
                // Val cannot be reassigned
                // destinationFile = file("$buildDir/jacoco/module.exec")
                setDestinationFile(file("$buildDir/jacoco/module.exec"))
            }
        }

        dependencies {
            add("testImplementation", kotlinxCoroutinesTest.dep())
            add("testImplementation", mockk.dep())
            add("testImplementation", jupiterApi.dep())
            add("testRuntimeOnly", jupiterEngine.dep())
            add("testRuntimeOnly", junitPlatformLauncher.dep())
        }
    }

    private fun Project.coverage() {
        pluginManager.apply(JacocoPlugin::class)
    }
}
