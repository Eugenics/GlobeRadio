plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 27
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("androidx.media:media:1.6.0")

    // Media3
    val media3Version = "1.0.0-beta01"

    // For media playback using ExoPlayer
    implementation("androidx.media3:media3-exoplayer:$media3Version")

    // For DASH playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-dash:$media3Version")
    // For HLS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
    // For RTSP playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-rtsp:$media3Version")
    // For loading data using the OkHttp network stack
    implementation("androidx.media3:media3-datasource-okhttp:$media3Version")
    // For building media playback UIs
    implementation("androidx.media3:media3-ui:$media3Version")
    // For exposing and controlling media sessions
    implementation("androidx.media3:media3-session:$media3Version")
    // For scheduling background operations using Jetpack Work's WorkManager with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-workmanager:$media3Version")

    val exoplayer_version = "2.18.1"

    implementation("com.google.android.exoplayer:exoplayer:$exoplayer_version")
    implementation("com.google.android.exoplayer:extension-mediasession:$exoplayer_version")

    // Glide dependencies
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.11.0")

    // Google GSON
    implementation("com.google.code.gson:gson:2.9.0")

    //Room
    // To use Kotlin annotation processing tool (kapt)
    val roomVersion = "2.4.2"
    kapt("androidx.room:room-compiler:$roomVersion")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$roomVersion")
    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$roomVersion")
    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$roomVersion")

    // KotlinX Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    // Retrofit
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
}