import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.personal.accountantAssistant"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    signingConfigs {
        register("release") {
            storeFile =
                file("${rootProject.projectDir.absolutePath}/${localProperties["storeFileName"]}")
            storePassword = localProperties["storePassword"]?.toString() ?: ""
            keyAlias = localProperties["keyAlias"]?.toString() ?: ""
            keyPassword = localProperties["keyPassword"]?.toString() ?: ""
        }
    }
    defaultConfig {
        applicationId = "com.personal.accountantAssistant"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            manifestPlaceholders += mapOf("ADMOB_APP_ID" to "${localProperties["admobAppId"]}")
            buildConfigField(
                type = "String",
                name = "admobUnitId",
                value = "\"${localProperties["admobUnitId"]}\""
            )
            buildConfigField(
                type = "String",
                name = "admobTestDeviceId",
                value = "\"${localProperties["admobTestDeviceId"]}\""
            )
            setProguardFiles(
                listOf(
                    getDefaultProguardFile(name = "proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
            signingConfig = signingConfigs.getByName(name = "release")
        }
        named("debug") {
            manifestPlaceholders["ADMOB_APP_ID"] = "${localProperties["admobDebugAppId"]}"
            buildConfigField(
                type = "String",
                name = "admobUnitId",
                value = "\"${localProperties["admobDebugUnitId"]}\""
            )
            buildConfigField(
                type = "String",
                name = "admobTestDeviceId",
                value = "\"${localProperties["admobDebugTestDeviceId"]}\""
            )
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0"
            )
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {

    //---Kotlin dependencies-----------------------------------------------
    implementation(libs.kotlin.stdlib.jdk7)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.play.services)

    //----------Core dependencies----------------------
    implementation(libs.core.ktx)
    implementation(libs.annotation)
    implementation(libs.appcompat)
    implementation(libs.legacy.support.v4)
    implementation(libs.constraintlayout)
    implementation(libs.fragment.ktx)

    //Koin for dependency injection
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.android.compat)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.workmanager)
    testImplementation(libs.koin.test)

    // Coroutines dependencies
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    //----------LifeCycle dependencies----------------------
    implementation(libs.lifecycle.extensions)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    //----Navigation------------------------------------------------
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.navigation.compose)

    //----Room------------------------------------------------
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    //----------Recycler view dependencies----------------------
    implementation(libs.recyclerview)

    //-------------Google dependencies-------------------------
    implementation(libs.material)
    implementation(libs.play.services.ads)
    // CameraX
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    // ML Kit
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.mlkit.text.recognition)
    implementation(libs.play.services.auth)
    implementation(libs.google.http.client.gson)
    implementation(libs.google.api.client.android) {
        exclude(group = "org.apache.httcomponents")
        exclude(group = "org.apache.commons", module = "commons-logging")
    }
    implementation(libs.google.api.services.drive) {
        exclude(group = "org.apache.httcomponents")
        exclude(group = "org.apache.commons", module = "commons-logging")
    }

    //-------------Firebase dependencies-------------------------
    implementation(libs.firebase.perf.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.messaging.directboot)
    implementation(libs.firebase.database.ktx)

    // WorkManager Android Job
    implementation(libs.androidx.work.runtime.ktx)

    //------------Glide dependencies---------------
    implementation(libs.glide)
    ksp(libs.compiler)

    //-----Other dependencies---------------
    implementation(libs.jxl)
    implementation(libs.javax.mail.api)
    implementation(libs.mpAndroidChart)

    //----------Compose dependencies----------------------
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.compose.foundation)
    implementation(libs.activity.compose)
    debugImplementation(libs.compose.ui.tooling)

    //-----Test dependencies---------------
    testImplementation(libs.junit.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.core.ktx)
}