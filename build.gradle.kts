buildscript {
    val agp_version by extra("8.1.0")
    dependencies {
        classpath(libs.hilt.plugin)
        classpath(libs.kotlin.serialization)
        classpath("com.android.tools.build:gradle:$agp_version")
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
