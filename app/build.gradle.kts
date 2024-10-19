import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.mucheng.qingyuan.wallpaper"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mucheng.qingyuan.wallpaper"
        minSdk = 24
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 28
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        create("shared") {
            storeFile = file("../buildKey.jks")
            keyAlias = "CaiMuCheng"
            storePassword = "L8855116900"
            keyPassword = "L8855116900"

            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs["shared"]
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
        debug {
            signingConfig = signingConfigs["shared"]
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    composeCompiler {
        featureFlags = setOf(
            ComposeFeatureFlag.OptimizeNonSkippingGroups
        )
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kotlin {
        jvmToolchain(21)
    }
}

dependencies {
    annotationProcessor(libs.compiler)
    implementation(libs.compose)
    implementation(libs.okhttp)
    implementation(libs.androidx.paging.compose)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}