import com.android.utils.jvmArchitecture
import org.gradle.kotlin.dsl.support.kotlinCompilerOptions

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }

    }
    kotlinOptions {
        jvmTarget = "12"

    }

}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation("org.zwobble.mammoth:mammoth:1.5.0") {
        exclude(group = "org.slf4j", module = "slf4j-simple")
    }
    implementation("nl.siegmann.epublib:epublib-core:3.1") {
        exclude(group = "org.slf4j", module = "slf4j-simple")
        exclude(group = "org.slf4j", module = "slf4j-nop")
        exclude(group = "xmlpull", module = "xmlpull")
    }
    implementation("org.slf4j:slf4j-android:1.7.25")
    implementation("com.itextpdf:kernel:8.0.5")
    implementation("com.itextpdf:io:8.0.5")
    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation(libs.android.image.cropper)
    implementation("com.vanniktech:android-image-cropper:4.6.0")
    implementation("com.github.yalantis:ucrop:2.2.6")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")

}