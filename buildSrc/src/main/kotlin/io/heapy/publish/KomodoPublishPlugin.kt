package io.heapy.publish

import io.heapy.Extensions.defaultRepositories
import io.heapy.Libs.getKomodoLibraries
import io.heapy.Lib
import io.heapy.Libs.libraries
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.AwsCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.dokka.gradle.DokkaTask

/**
 * Plugin which setups defaults for publishing
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class KomodoPublishPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.defaultRepositories()
        project.pluginManager.apply("signing")
        project.pluginManager.apply("java-library")
        project.pluginManager.apply("maven-publish")
        project.pluginManager.apply("org.jetbrains.dokka")

        val isBOM = project.name == "komodo-bom"

        // Move extensions to afterEvaluate phase, since group values
        // not available before evaluation of projects
        project.afterEvaluate {
            lateinit var komodoPublication: MavenPublication

            project.extensions.getByType<PublishingExtension>().apply {
                repositories {
                    maven {
                        url = uri("s3://heapy")
                        credentials(AwsCredentials::class.java) {
                            accessKey = System.getenv("AWS_ACCESS_KEY_ID")
                            secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
                        }
                    }
                }

                publications {
                    komodoPublication = create<MavenPublication>("komodo") {
                        if (!isBOM) {
                            from(project.components["java"])
                        }

                        groupId = project.group.toString()
                        artifactId = project.name
                        version = project.version.toString()

                        if (!isBOM) {
                            artifact(project.sourcesJar())
                            val dokkaVerySlow = true
                            if (!dokkaVerySlow) {
                                artifact(project.dokkaJavadocJar())
                                artifact(project.dokkaHtmlJar())
                            }
                        }

                        pom {
                            name.set(project.name)
                            description.set(project.description)
                            url.set("https://heapy.io/komodo")

                            licenses {
                                license {
                                    name.set("LGPL-3.0-or-later")
                                    url.set("https://www.gnu.org/licenses/lgpl-3.0.en.html")
                                }
                            }

                            developers {
                                developer {
                                    id.set("IRus")
                                    name.set("Ruslan Ibragimov")
                                    email.set("ruslan@ibragimov.by")
                                }
                            }

                            scm {
                                connection.set("scm:git:https://github.com/Heapy/komodo.git")
                                developerConnection.set("scm:git:git@github.com:Heapy/komodo.git")
                                url.set("https://github.com/Heapy/komodo")
                            }

                            issueManagement {
                                system.set("GitHub")
                                url.set("https://github.com/heapy/komodo/issues")
                            }

                            addDependencies(isBOM, project)
                        }
                    }
                }
            }

            project.extensions.getByType<SigningExtension>().apply {
                setRequired(false)
                sign(komodoPublication)
            }
        }
    }

    private fun MavenPom.addDependencies(isBOM: Boolean, project: Project) {
        if (isBOM) {
            val komodoLibraries = getKomodoLibraries(project.version.toString())

            // Validate that manual change to komodoLibs was made,
            // and Lin either published, or not
            project.validateKomodoLibs(komodoLibraries)

            packaging = "pom"

            withXml {
                val dependencies = asNode()
                    .appendNode("dependencyManagement")
                    .appendNode("dependencies")

                val komodoLibrariesPublish = komodoLibraries.filter { it.publish }
                (libraries + komodoLibrariesPublish).forEach {
                    val dependency = dependencies.appendNode("dependency")
                    dependency.appendNode("groupId", it.group)
                    dependency.appendNode("artifactId", it.artifact)
                    dependency.appendNode("version", it.version)
                }
            }
        }
    }

    private fun Project.sourcesJar(): TaskProvider<Jar> {
        return project.tasks.named<Jar>("kotlinSourcesJar")
    }

    private fun Project.dokkaJavadocJar(): TaskProvider<Jar> {
        return project.tasks.register<Jar>("dokkaJavadocJar") {
            group = "documentation"
            val dokkaJavadoc = project.tasks.named<DokkaTask>("dokkaJavadoc")
            dependsOn(dokkaJavadoc)
            from(dokkaJavadoc.flatMap { it.outputDirectory })
            archiveClassifier.set("javadoc")
        }
    }

    private fun Project.dokkaHtmlJar(): TaskProvider<Jar> {
        return project.tasks.register<Jar>("dokkaHtmlJar") {
            group = "documentation"
            val dokkaHtml = project.tasks.named<DokkaTask>("dokkaHtml")
            dependsOn(dokkaHtml)
            from(dokkaHtml.flatMap { it.outputDirectory })
            archiveClassifier.set("html-doc")
        }
    }

    private fun Project.validateKomodoLibs(libs: List<Lib>) {
        val definedLibs = libs.map { it.artifact }
        val knownLibs = rootProject.allprojects.map { it.name }

        val unknown = knownLibs.subtract(definedLibs)

        if (unknown.isNotEmpty()) {
            throw KomodoPublishException("Unknown libraries found: $unknown")
        }
    }

    private class KomodoPublishException(
        override val message: String
    ) : GradleException()
}
