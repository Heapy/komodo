group = "io.heapy.komodo.integration"

apply(from = "$rootDir/publish.gradle")

dependencies {
    implementation(project(":komodo-logging"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.2.1")

    implementation("org.junit.platform:junit-platform-engine")
    implementation("org.junit.jupiter:junit-jupiter-api")
    implementation("org.junit.platform:junit-platform-commons")

    testImplementation("io.mockk:mockk")

    testRuntimeOnly("org.slf4j:slf4j-simple")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.platform:junit-platform-testkit")
}
