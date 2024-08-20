import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

val localProperties = Properties().apply {
    load(File(rootProject.projectDir, "local.properties").inputStream())
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
            manifestPlaceholders["ADMOB_APP_ID"] = "${localProperties["admobAppId"]}"
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
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
    implementation(libs.koin.android.viewmodel)
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

    //----Room------------------------------------------------
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    //----------Recycler view dependencies----------------------
    implementation(libs.recyclerview)

    //-------------Google dependencies-------------------------
    implementation(libs.material)
    implementation(libs.play.services.ads)
    implementation(libs.play.services.vision)
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

    // WorkManager Android Job
    implementation(libs.androidx.work.runtime.ktx)

    //------------Glide dependencies---------------
    implementation(libs.glide)
    ksp(libs.compiler)

    //-----Other dependencies---------------
    implementation(libs.jxl)
    implementation(libs.javax.mail.api)
    implementation(libs.mpAndroidChart)

    //-----Test dependencies---------------
    testImplementation(libs.junit.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.core.ktx)
}