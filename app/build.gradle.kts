plugins {
    androidApplication
    kotlin
    kapt
    parcelize
    serialization
    dagger
}

android {
    namespace = "com.eugenics.freeradio"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "com.eugenics.core_testing.HiltTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true          // С 8 версии AGP делать вручную
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {

    // Modules
    implementation(project(":core"))
    implementation(project(":core_data"))
    implementation(project(":media_service"))
    androidTestImplementation(project(":core_testing"))

    //Libs
    implementation(files("libs/material-colors-util.jar"))

    implementation(Deps.AndroidX.coreKtx)
    implementation(Deps.Google.material)
    implementation(Deps.Compose.composeUi)
    implementation(Deps.Compose.composeTooling)
    implementation(Deps.Compose.composePreview)
    implementation(Deps.AndroidX.lifecycleRuntime)
    implementation(Deps.AndroidX.activityCompose)
    implementation(Deps.Compose.animationGraphics)
    implementation(Deps.AndroidX.pallete)

    //GSON
    implementation(Deps.Google.gson)

    // Hilt - dependency injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigationcompose)

    // Kotlin
    implementation(Deps.KotlinX.kotlinStdlibJdk8)
    implementation(Deps.KotlinX.coroutinesCore)
    implementation(Deps.KotlinX.coroutinesAndroid)
    implementation(Deps.KotlinX.kotlinxSerialization)

    // Navigation
    implementation(Deps.AndroidX.navigation)

    // Compose Accompanist
    implementation(Deps.Accomponist.inserts)
    implementation(Deps.Accomponist.coil)
    implementation(Deps.Accomponist.swipe)
    implementation(Deps.Accomponist.systemUiController)
    implementation(Deps.Accomponist.navigaionAnimation)

    //Coil compose
    implementation(Deps.Images.coil)

    // Media
    implementation(Deps.AndroidX.media)

    // Material 3
    implementation(Deps.Material3.material)
    implementation(Deps.Material3.windowSize)

    // DataStore
    implementation(Deps.DataStore.datastore)

    // Instrumental tests
    androidTestImplementation(Deps.JUnit.junitExt)
    androidTestImplementation(Deps.Espresso.core)
    androidTestImplementation(Deps.AndroidX.testCore)
    androidTestImplementation(Deps.AndroidX.testRunner)
    androidTestImplementation(Deps.Hilt.testing)
    kaptAndroidTest(Deps.Hilt.compiler)

    // Local tests
    testImplementation(Deps.JUnit.junit)
    testImplementation(Deps.AndroidX.testCore)
    testImplementation(Deps.AndroidX.lifecycleViewModelCompose)
    testImplementation(Deps.Mockito.core)
    testImplementation(Deps.Mockito.mockitoKotlin)
    testImplementation(Deps.Mockito.mocc)
    testImplementation(Deps.KotlinX.kotlinxCoroutinesTest)
    testImplementation(Deps.Hilt.testing)

    // Compose testing dependencies
    androidTestImplementation(Deps.Compose.uiTesting)
    androidTestImplementation(Deps.Compose.jUnitTesting)
    debugImplementation(Deps.Compose.uiTestManifest)
}