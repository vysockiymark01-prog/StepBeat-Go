// Top-level build file.
// Versions here follow AGP 9's requirements (AGP 9.0.1 has a runtime
// dependency on Kotlin 2.2.10 and Gradle 9.1.0+ — see
// https://developer.android.com/build/releases/agp-9-0-0-release-notes).
plugins {
    id("com.android.application") version "9.0.1" apply false
    id("org.jetbrains.kotlin.android") version "2.2.10" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
    id("com.google.devtools.ksp") version "2.2.10-2.0.2" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
