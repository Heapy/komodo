import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask
import java.time.LocalDate

plugins {
    id("org.asciidoctor.jvm.convert").version("3.1.0")
    id("org.asciidoctor.jvm.pdf").version("3.1.0")
}

repositories {
    jcenter()
}

/**
 * User Guide
 * https://asciidoctor.github.io/asciidoctor-gradle-plugin/development-3.x/user-guide/
 */
val asciidoctor by tasks.existing(AsciidoctorTask::class) {
    val docs = rootProject.projectDir.resolve("docs")

    setSourceDir(docs)
    baseDirFollowsSourceDir()

    sources {
        include("index.adoc")
    }

    resources {
        from(docs) {
            include("*.png")
        }
    }

    attributes = mapOf(
        "komodo-version" to version,
        "revnumber" to version,
        "revdate" to LocalDate.now(),
        "toc" to "left"
    )

    setOutputDir(buildDir.resolve("dist").resolve("$version"))
}

val asciidoctorPdf by tasks.existing(AsciidoctorPdfTask::class) {
    val docs = rootProject.projectDir.resolve("docs")

    setSourceDir(docs)
    baseDirFollowsSourceDir()

    sources {
        include("index.adoc")
    }

    attributes = mapOf(
        "komodo-version" to version,
        "revnumber" to version,
        "revdate" to LocalDate.now()
    )

    setOutputDir(buildDir.resolve("dist").resolve("$version"))
}
