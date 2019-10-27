import io.heapy.Extensions.kotlinRepositories

pluginManagement {
    repositories {
        jcenter()
        gradlePluginPortal()
        kotlinRepositories()
    }
}

rootProject.name = "komodo"

fun modules(vararg paths: String) {
    paths.forEach { path ->
        val name = path.substringAfterLast('/')
        include(name)
        project(":$name").projectDir = file(path)
    }
}

modules(
    "komodo-bom",
    "komodo-config",
    "komodo-config-dotenv",
    "komodo-core",
    "komodo-core-coroutines",
    "komodo-core-concurrent",
    "komodo-core-beans",
    "komodo-di",
    "komodo-di-default",
    "komodo-env",
    "komodo-logging"
)
