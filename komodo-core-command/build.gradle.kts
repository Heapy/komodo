group = "io.heapy.komodo"

plugins {
    id("io.heapy.build.jvm")
    id("io.heapy.publish")
}

dependencies {
    implementation(project(":komodo"))
}
