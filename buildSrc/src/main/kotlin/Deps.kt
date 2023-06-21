import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

object BaseConfig {
    const val jvmTarget = "11"
    const val compileSdk = 33
    const val minSdk = 27
}

object Deps {
    object Android {}

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.10.1"
        const val appcompat = "androidx.appcompat:appcompat:1.6.1"
        const val media = "androidx.media:media:1.6.0"
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

        private const val kotlinCoroutinesVersion = "1.7.1"
        const val kotlinxCoroutinesTest =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion"
        const val coroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion"
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinCoroutinesVersion"
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

    object Exoplayer {
        private const val exoplayerVersion = "2.18.1"
        const val exoplayer = "com.google.android.exoplayer:exoplayer:$exoplayerVersion"
        const val extOkhttp = "com.google.android.exoplayer:extension-okhttp:$exoplayerVersion"
        const val extMediasession =
            "com.google.android.exoplayer:extension-mediasession:$exoplayerVersion"
    }

    object Images {
        const val glide = "com.github.bumptech.glide:glide:4.12.0"
        const val coil = "io.coil-kt:coil-compose:2.2.0"
    }
}

private typealias pdss = PluginDependenciesSpec
private typealias pds = PluginDependencySpec

inline val pdss.androidLibrary: pds get() = id("com.android.library")
inline val pdss.kotlin: pds get() = id("org.jetbrains.kotlin.android")
inline val pdss.parcelize: pds get() = id("kotlin-parcelize")
inline val pdss.serialization: pds get() = id("kotlinx-serialization")
inline val pdss.kapt: pds get() = id("kotlin-kapt")
inline val pdss.dagger: pds get() = id("dagger.hilt.android.plugin")