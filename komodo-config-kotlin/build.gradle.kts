dependencies {
    api(project(":komodo-scripting"))
    api(project(":komodo"))

    // kotlin & coroutines
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
}
