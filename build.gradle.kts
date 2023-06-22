buildscript {
    dependencies {
        classpath(Deps.Hilt.androidPlugin)
        classpath(Deps.KotlinX.kotlinSerialization)
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.4.2" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.21" apply false
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.20" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
