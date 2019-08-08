dependencies {
    compile(project(":komodo-env"))
    compile(project(":komodo-core"))

    // kotlin & coroutines
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
}
