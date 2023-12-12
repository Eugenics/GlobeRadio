import org.jetbrains.kotlin.kapt.cli.main
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

plugins {
    androidApplication
    kotlin
    kapt
    parcelize
    serialization
    dagger
    libs.plugins.google.services
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

    // Firebase
    implementation(libs.google.firebase)

    // Workmanager
    implementation(libs.androidx.workmanager)

    // Splash Screen
    implementation(libs.androidx.core.splash)
}