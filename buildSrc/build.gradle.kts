plugins {
    `kotlin-dsl`
}

val kotlinVersion: String by project
val dokkaVersion: String by project

repositories {
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}
