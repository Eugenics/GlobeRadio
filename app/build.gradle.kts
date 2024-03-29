import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.ksp.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id(libs.plugins.kotlinx.serialization.get().pluginId)
    id(libs.plugins.dagger.hilt.get().pluginId)
    id(libs.plugins.google.services.get().pluginId)
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.eugenics.freeradio"
    compileSdk = libs.versions.compileSdk.get().toInt()

    val versionFile = rootProject.file("versions.properties")
    val versionProperties = Properties()
    versionProperties.load(FileInputStream(versionFile))
    val buildCode = versionProperties.getProperty("build_version").toInt() + 1

    versionProperties["build_version"] = buildCode.toString()
    versionProperties.store(FileOutputStream(versionFile), "new build version")

    val versionBuildName = "${libs.versions.versionName.get()}.${buildCode}"
    val versionFileName = "globe-radio_${versionBuildName}"

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = buildCode
        versionName = versionBuildName
        testInstrumentationRunner = "com.eugenics.core_testing.HiltTestRunner"
        setProperty("archivesBaseName", versionFileName)
    }

    signingConfigs {
        create("release") {
            val sensitiveFile = rootProject.file("sensitive.properties")
            val sensitiveProperties = Properties()
            sensitiveProperties.load(FileInputStream(sensitiveFile))

            storeFile = File("${rootDir.path}//keystore.jks")
            storePassword = sensitiveProperties.getProperty("release_password")
            keyAlias = sensitiveProperties.getProperty("key_alias")
            keyPassword = sensitiveProperties.getProperty("release_password")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "./core_network/retrofit2.pro"
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
    packaging {
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

    //sourceSets["main"].resources.srcDirs("../resource/src/main/res")
}

dependencies {

    // Modules
    implementation(project(":core"))
    implementation(project(":core_data"))
    implementation(project(":media_service"))
    androidTestImplementation(project(":core_testing"))
    implementation(project(":resource"))
    implementation(project(":ui_core"))
    implementation(project(":ui"))

    implementation(libs.androidx.ktx)
    implementation(libs.google.material)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.pallete)

    // Compose
    implementation(libs.bundles.compose)

    //GSON
    implementation(libs.google.gson)

    // Hilt - dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigationcompose)

    // Kotlin
    implementation(libs.kotlin.stdlibjdk8)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Compose Accompanist
    implementation(libs.accomponist.inserts)
    implementation(libs.accomponist.coil)
    implementation(libs.accomponist.swipe)
    implementation(libs.accomponist.systemUiController)
    implementation(libs.accomponist.navigaionAnimation)

    //Coil compose
    implementation(libs.coil)

    // Media
    implementation(libs.androidx.media)

    // Material 3
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.windowsize)

    // DataStore
    implementation(libs.androidx.datastore)

    // Instrumental tests
    androidTestImplementation(Deps.JUnit.junitExt)
    androidTestImplementation(Deps.Espresso.core)
    androidTestImplementation(Deps.AndroidX.testCore)
    androidTestImplementation(Deps.AndroidX.testRunner)
    androidTestImplementation(Deps.Hilt.testing)
    kspAndroidTest(libs.hilt.compiler)

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

    // Firebase
    implementation(libs.google.firebase)

    // Workmanager
    implementation(libs.androidx.workmanager)

    // Splash Screen
    implementation(libs.androidx.core.splash)

    // Firebase Crashlytics
    implementation("com.google.firebase:firebase-crashlytics:18.6.0")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
}