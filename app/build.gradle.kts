plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.sooum.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sooum.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 6
        versionName = "2.13"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //navigation
    val nav_version = "2.5.3"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    //Coil
    implementation("io.coil-kt:coil-compose:2.3.0")

    //Retrofit2
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //Pull to Refresh
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.36.0")
    implementation("androidx.compose.material:material:1.7.3")

    //viewmodel livedata(추후에 변경할듯)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.compose.runtime:runtime-livedata:1.3.2")
    implementation ("androidx.activity:activity-ktx:1.1.0")

    //GPS
    implementation("com.google.android.gms:play-services-location:21.3.0")

    //flowRow
    implementation ("com.google.accompanist:accompanist-flowlayout:0.30.1")

    //Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Paging3 라이브러리
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")

    // Compose와 Paging3 통합을 위한 라이브러리
    implementation("androidx.paging:paging-compose:3.3.2")

    // Image Cropper
    implementation("com.vanniktech:android-image-cropper:4.5.0")

    // OkHttp 및 HttpLoggingInterceptor
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    //haze
    implementation("dev.chrisbanes.haze:haze:1.0.2")

    //fcm
    implementation ("com.google.firebase:firebase-messaging-ktx")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
}