plugins {
    `kotlin-dsl`
}

val kotlinVersion: String by project
val dokkaVersion: String by project
val bintrayVersion: String by project

repositories {
    jcenter()
    gradlePluginPortal()
    when {
        kotlinVersion.contains("dev") -> maven { url = uri("https://dl.bintray.com/kotlin/kotlin-dev/") }
        kotlinVersion.contains("eap") -> maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap/") }
    }
}

dependencies {
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:$bintrayVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}
