rootProject.name = "komodo-root"

fun modules(vararg paths: String) {
    paths.forEach { path ->
        val name = path.substringAfterLast('/')
        include(name)
        project(":$name").projectDir = file(path)
    }
}

modules(
    "komodo",
    "komodo-bom",
    "komodo-config",
    "komodo-config-dotenv",
    "komodo-core-beans",
    "komodo-core-cli",
    "komodo-core-command",
    "komodo-core-concurrent",
    "komodo-core-coroutines",
    "komodo-di",
    "komodo-docs",
    "komodo-logging",
    "komodo-utils/komodo-deferrify"
)
