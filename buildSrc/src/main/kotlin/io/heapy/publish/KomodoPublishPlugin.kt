package io.heapy.publish

import com.jfrog.bintray.gradle.BintrayExtension
import io.heapy.Extensions.defaultRepositories
import io.heapy.Libs.ALL
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.dokka.gradle.DokkaTask
import java.util.Date

/**
 * Plugin which setups defaults for publishing
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class KomodoPublishPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.defaultRepositories()
        project.plugins.apply("signing")
        project.plugins.apply("java-library")
        project.plugins.apply("maven-publish")
        project.plugins.apply("com.jfrog.bintray")
        project.plugins.apply("org.jetbrains.dokka")

        val sourcesJar = project.tasks.create("sourcesJar", Jar::class) {
            group = "documentation"
            val sourceSets: SourceSetContainer by project
            from(sourceSets["main"].allSource)
            archiveClassifier.set("sources")
        }

        val dokka = project.tasks.getByName<DokkaTask>("dokka") {
            outputFormat = "html"
            outputDirectory = "${project.buildDir}/dokka"
        }

        val dokkaJar = project.tasks.create("dokkaJar", Jar::class) {
            group = "documentation"
            dependsOn(dokka)
            from(dokka.outputDirectory)
            archiveClassifier.set("javadoc")
        }

        val isBOM = project.name == "komodo-bom"

        // Move extensions to afterEvaluate phase, since group values
        // not available before evaluation of projects
        project.afterEvaluate {
            lateinit var komodoPublication: MavenPublication

            project.extensions.getByType<PublishingExtension>().apply {
                publications {
                    komodoPublication = create<MavenPublication>("komodo") {
                        if (!isBOM) {
                            from(project.components["java"])
                        }

                        groupId = project.group.toString()
                        artifactId = project.name
                        version = project.version.toString()

                        if (!isBOM) {
                            artifact(sourcesJar)
                            artifact(dokkaJar)
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

                            addDependencies(isBOM)
                        }
                    }
                }
            }

            project.extensions.getByType<SigningExtension>().apply {
                setRequired(false)
                sign(komodoPublication)
            }

            val isDev = project.version.toString().contains("development")

            project.extensions.getByType<BintrayExtension>().apply {
                user = System.getenv("BINTRAY_USER")
                key = System.getenv("BINTRAY_API_KEY")

                pkg = PackageConfig().apply {
                    userOrg = "heapy"
                    repo = if (isDev) "heap-dev" else "releases"
                    name = "komodo"
                    websiteUrl = "https://heapy.io/komodo"
                    publish = isDev
                    setLicenses("LGPL-3.0")
                    vcsUrl = "https://github.com/Heapy/komodo"
                    setPublications("komodo")
                    publicDownloadNumbers = true
                    version = VersionConfig().apply {
                        name = project.version.toString()
                        released = Date().toString()
                        vcsTag = project.version.toString()
                    }
                }
            }
        }
    }

    private fun MavenPom.addDependencies(isBOM: Boolean) {
        if (isBOM) {
            packaging = "pom"

            withXml {
                val dependencies = asNode()
                    .appendNode("dependencyManagement")
                    .appendNode("dependencies")

                ALL.forEach {
                    val dependency = dependencies.appendNode("dependency")
                    dependency.appendNode("groupId", it.group)
                    dependency.appendNode("artifactId", it.artifact)
                    dependency.appendNode("version", it.version)
                }
            }
        }
    }
}
