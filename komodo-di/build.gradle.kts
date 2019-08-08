
repositories {
    maven { url = uri("http://maven.objectstyle.org/nexus/content/repositories/bootique-snapshots/") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.google.inject:guice:4.2.2")
    implementation("org.springframework:spring-context:5.1.6.RELEASE")
    implementation("org.koin:koin-core:2.0.0-rc-3")
    implementation("org.koin:koin-core-ext:2.0.0-rc-3")
    implementation("io.bootique.di:bootique-di:1.0-SNAPSHOT")
}
