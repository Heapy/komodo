plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.10.0")
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50")
}
