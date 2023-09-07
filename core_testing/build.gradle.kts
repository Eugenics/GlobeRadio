plugins {
    androidLibrary
    kotlin
}

android {
    namespace = "com.eugenics.core_testing"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("androidx.test:core-ktx:1.5.0")
    implementation("androidx.test:runner:1.5.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Dagger
    val daggerVersion = "2.47"
    implementation("com.google.dagger:dagger:$daggerVersion")

    // Hilt
    implementation("com.google.dagger:hilt-android:$daggerVersion")
    implementation("com.google.dagger:hilt-android-testing:$daggerVersion")
}