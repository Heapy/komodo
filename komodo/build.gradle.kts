import io.heapy.Libs.kotlinReflect

group = "io.heapy.komodo"

plugins {
    id("io.heapy.build.jvm")
    id("io.heapy.publish")
}

dependencies {
    api(project(":komodo-di"))
    implementation(kotlinReflect.dep)
}
