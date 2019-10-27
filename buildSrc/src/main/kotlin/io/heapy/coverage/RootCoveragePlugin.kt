package io.heapy.coverage

import io.heapy.Extensions.defaultRepositories
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoMerge
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Plugin which setups defaults in jvm based komodo modules
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class RootCoveragePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.defaultRepositories()
        project.config()
    }

    private fun Project.config() {
        plugins.apply("jacoco")

        extensions.getByType(JacocoPluginExtension::class.java).apply {
            toolVersion = "0.8.5"
        }

        val jacocoMerge by tasks.registering(JacocoMerge::class) {
            group = "jacoco"
            description = "JaCoCo merge subprojects results into root one"

            destinationFile = file("$buildDir/jacoco/rootTestsCoverage.exec")
            executionData = files(subprojects.mapNotNull {
                val exec = file("${it.buildDir}/jacoco/module.exec")
                if (exec.exists()) exec else null
            })
        }

        @Suppress("UNUSED_VARIABLE")
        val jacocoRootReport by tasks.registering(JacocoReport::class) {
            group = "jacoco"
            description = "Generate Jacoco coverage reports after running tests."

            dependsOn(jacocoMerge)

            reports {
                xml.apply {
                    isEnabled = true
                    destination = file("${buildDir}/reports/jacoco/report.xml")
                }

                html.isEnabled = true
            }

            val mainSourceSets = subprojects.mapNotNull {
                val sourceSets: SourceSetContainer? by it
                sourceSets?.get("main")
            }

            sourceDirectories.from(files(mainSourceSets.map { it.allSource.srcDirs }))
            classDirectories.from(files(mainSourceSets.map { it.output }))
            executionData.from(jacocoMerge.get().destinationFile)
        }
    }
}
