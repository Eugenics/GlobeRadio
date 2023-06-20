import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

object AppConfig {
    const val jvmTarget = "11"
    const val namespace = "com.eugenics.core"
    const val compileSdk = 33
    const val minSdk = 27
}

object Deps {
    object Android {}

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.10.1"
        const val appcompat = "androidx.appcompat:appcompat:1.6.1"
    }

    object JUnit {
        const val junit = "junit:junit:4.13.2"
        const val junitExt = "androidx.test.ext:junit:1.1.5"
    }

    object Espresso {
        const val core = "androidx.test.espresso:espresso-core:3.5.1"
    }

    object Hilt {
        private const val hiltVersion = "2.45"
        const val hilt = "com.google.dagger:hilt-android:$hiltVersion"
        const val compiler = "com.google.dagger:hilt-android-compiler:$hiltVersion"
        const val testing = "com.google.dagger:hilt-android-testing:$hiltVersion"
    }

    object Dagger {
        private const val daggerVersion = "2.45"
        const val dagger = "com.google.dagger:dagger:$daggerVersion"
        const val compiler = "com.google.dagger:dagger-compiler:$daggerVersion"
    }

    object Room {
        private const val roomVersion = "2.4.2"
        const val compiler = "androidx.room:room-compiler:$roomVersion"
        const val room = "androidx.room:room-ktx:$roomVersion"
        const val testing = "androidx.room:room-testing:$roomVersion"
        const val paging = "androidx.room:room-paging:$roomVersion"

    }

    object KotlinX {
        private const val kotlinxSerializationVersion = "1.5.0"
        const val kotlinxSerialization =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion"
    }

    object Google {
        private const val gsonVersion = "2.10.1"
        const val gson = "com.google.code.gson:gson:$gsonVersion"
    }

    object Retrofit {
        private const val retrofitVersion = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:$retrofitVersion"
        const val serializationConverter =
            "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
        const val intercepter = "com.squareup.okhttp3:logging-interceptor:4.11.0"
    }
}

private typealias pdss = PluginDependenciesSpec
private typealias pds = PluginDependencySpec

inline val pdss.androidLibrary:pds get() = id("com.android.library")
inline val pdss.kotlin:pds get() = id("org.jetbrains.kotlin.android")
inline val pdss.parcelize:pds get() = id("kotlin-parcelize")
inline val pdss.serialization:pds get() = id("kotlinx-serialization")
inline val pdss.kapt:pds get() = id("kotlin-kapt")
inline val pdss.dagger:pds get() = id("dagger.hilt.android.plugin")