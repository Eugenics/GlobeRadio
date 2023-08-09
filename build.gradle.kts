buildscript {
    dependencies {
        classpath(libs.hilt.plugin)
        classpath(libs.kotlin.serialization)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.gradle.toolchain) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
