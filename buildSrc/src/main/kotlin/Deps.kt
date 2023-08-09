import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

object BaseConfig {
    const val jvmTarget = "11"
    const val compileSdk = 33
    const val minSdk = 27
    const val targetSdk = 33
    const val applicationId = "com.eugenics.freeradio"
    const val versionCode = 2
    const val versionName = "1.1"
    const val kotlinCompilerExtensionVersion = "1.4.8"
}

object Deps {
    object Android {}

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.10.1"
        const val appcompat = "androidx.appcompat:appcompat:1.6.1"
        const val media = "androidx.media:media:1.6.0"
        const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:2.6.1"
        const val lifecycleViewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1"
        const val activityCompose = "androidx.activity:activity-compose:1.7.2"
        const val pallete = "androidx.palette:palette:1.0.0"
        const val navigation = "androidx.navigation:navigation-compose:2.5.3"
        const val testCore = "androidx.test:core:1.5.0"
        const val testRunner = "androidx.test:runner:1.5.2"
    }

    object Compose {
        private const val composeVersion = "1.4.3"
        const val composeUi = "androidx.compose.ui:ui:$composeVersion"
        const val composeTooling = "androidx.compose.ui:ui-tooling:$composeVersion"
        const val composePreview = "androidx.compose.ui:ui-tooling-preview:$composeVersion"
        const val animationGraphics =
            "androidx.compose.animation:animation-graphics:$composeVersion"
        const val uiTesting = "androidx.compose.ui:ui-test:$composeVersion"
        const val jUnitTesting = "androidx.compose.ui:ui-test-junit4:$composeVersion"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:$composeVersion"
    }

    object Accomponist {
        const val inserts = "com.google.accompanist:accompanist-insets:0.25.1"
        const val coil = "com.google.accompanist:accompanist-coil:0.15.0"
        const val swipe = "com.google.accompanist:accompanist-swiperefresh:0.25.1"
        const val systemUiController =
            "com.google.accompanist:accompanist-systemuicontroller:0.28.0"
        const val navigaionAnimation =
            "com.google.accompanist:accompanist-navigation-animation:0.30.0"
    }

    object Material3 {
        private const val material3Version = "1.1.1"
        const val material = "androidx.compose.material3:material3:$material3Version"
        const val windowSize =
            "androidx.compose.material3:material3-window-size-class:$material3Version"
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
        const val navigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0"
        const val androidPlugin = "com.google.dagger:hilt-android-gradle-plugin:$hiltVersion"
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

        private const val kotlinVersion = "1.8.21"
        const val kotlinStdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
        const val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion"
    }

    object Google {
        private const val gsonVersion = "2.10.1"
        const val gson = "com.google.code.gson:gson:$gsonVersion"
        const val material = "com.google.android.material:material:1.9.0"
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

    object DataStore {
        const val datastore = "androidx.datastore:datastore:1.0.0"
    }

    object Mockito {
        const val core = "org.mockito:mockito-core:5.3.1"
        const val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:5.0.0"
        const val mocc = "io.mockk:mockk:1.13.5"
    }
}

private typealias pdss = PluginDependenciesSpec
private typealias pds = PluginDependencySpec

inline val pdss.androidApplication: pds get() = id("com.android.application")
inline val pdss.androidLibrary: pds get() = id("com.android.library")
inline val pdss.kotlin: pds get() = id("org.jetbrains.kotlin.android")
inline val pdss.parcelize: pds get() = id("kotlin-parcelize")
inline val pdss.serialization: pds get() = id("kotlinx-serialization")
inline val pdss.kapt: pds get() = id("kotlin-kapt")
inline val pdss.dagger: pds get() = id("dagger.hilt.android.plugin")