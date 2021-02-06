package io.heapy.publish

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Sets current version according CI environment.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class KomodoVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val branch = System.getenv("TRAVIS_BRANCH") ?: "develop"
        val buildNumber = System.getenv("TRAVIS_BUILD_NUMBER") ?: "0"
        val pullRequest = System.getenv("TRAVIS_PULL_REQUEST") ?: "false"

        // Not on master branch, or pull request
        if (branch != "master" || pullRequest != "false") {
            val currentVersion = project.version as String

            // Branch not master, and it's not PR
            // Branch master, and it's PR

            project.allprojects.forEach {
                it.version = "$currentVersion-development.${buildNumber.padStart(6, '0')}"
            }
        }

        println("Project version: ${project.version}")
    }
}
