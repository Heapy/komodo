import io.heapy.Libs.slf4jApi

group = "io.heapy.komodo"

plugins {
    id("io.heapy.build.jvm")
    id("io.heapy.publish")
}

dependencies {
    api(slf4jApi.dep)
}
