dependencies {
    api(project(":komodo-env"))
    api(project(":komodo-scripting"))
    api(project(":komodo-core"))

    // kotlin & coroutines
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
}
