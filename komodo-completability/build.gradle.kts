group = "io.heapy.komodo"

apply(from = "$rootDir/publish.gradle")

repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    // kotlin & coroutines
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")

    // logging
    implementation("org.slf4j:slf4j-api")

    // testing
    testImplementation("io.mockk:mockk")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.2.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.0-SNAPSHOT")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.0-SNAPSHOT")
    testRuntimeOnly("org.junit.platform:junit-platform-engine:1.5.0-SNAPSHOT")

    testRuntimeOnly("org.slf4j:slf4j-simple")
}
