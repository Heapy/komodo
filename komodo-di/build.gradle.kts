import io.heapy.Libs.kotlinReflect

group = "io.heapy.komodo"

plugins {
    id("io.heapy.build.jvm")
    id("io.heapy.publish")
}

dependencies {
    implementation(kotlinReflect.dep())

    implementation("com.google.inject:guice:4.2.3")
    implementation("org.springframework:spring-context:5.2.0.RELEASE")
    implementation("org.koin:koin-core:2.0.1")
    implementation("org.koin:koin-core-ext:2.0.1")
}
