plugins {
    `kotlin-dsl`
}

val kotlinVersion: String by project
val dokkaVersion: String by project
val bintrayVersion: String by project

repositories {
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:$bintrayVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}
