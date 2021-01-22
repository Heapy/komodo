group = "io.heapy.komodo"

plugins {
    id("io.heapy.build.jvm")
    id("io.heapy.publish")
}

dependencies {
    api(project(":komodo-di"))
    api(project(":komodo-logging"))
}
