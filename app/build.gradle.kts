import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import java.util.Base64

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.devtools.ksp")
  id("com.google.dagger.hilt.android")
}

android {
  namespace = "io.github.kirasok.alarmix"
  compileSdk = 34

  defaultConfig {
    applicationId = "io.github.kirasok.alarmix"
    minSdk = 31
    targetSdk = 34
    versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
    versionName = System.getenv("VERSION") ?: "0.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  signingConfigs {
    create("release") {
      val keystore = File(projectDir, System.getenv("KEYSTORE_PATH") ?: "keystore.keystore").apply {
        ensureParentDirsCreated()
        createNewFile()
        val base64: String = System.getenv("KEYSTORE")?.replace("\n", "") ?: "" // Base64 doesn't consider string valid if it has \n character
        writeBytes(Base64.getDecoder().decode(base64))
      }
      storeFile = keystore
      storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
      keyAlias = System.getenv("KEYSTORE_ALIAS") ?: ""
      keyPassword = System.getenv("KEYSTORE_ALIAS_PASSWORD") ?: ""
    }
  }


  buildTypes {
    release {
      isMinifyEnabled = true
      signingConfig = signingConfigs.getByName("release")
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_17.toString()
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.11"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.13.1")
  implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.24"))
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
  implementation("androidx.activity:activity-compose:1.9.0")
  implementation(platform("androidx.compose:compose-bom:2024.05.00"))
  implementation("androidx.compose.ui:ui:1.6.7")
  implementation("androidx.compose.ui:ui-graphics:1.6.7")
  implementation("androidx.compose.ui:ui-tooling-preview:1.6.7")
  implementation("androidx.compose.material3:material3:1.2.1")
  implementation("androidx.room:room-common:2.6.1")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.7")
  debugImplementation("androidx.compose.ui:ui-tooling:1.6.7")
  debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.7")

  // Compose dependencies
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
  implementation("androidx.navigation:navigation-compose:2.7.7")
  implementation("androidx.compose.material:material-icons-extended:1.6.7")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

  // Coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

  //Dagger - Hilt
  implementation("com.google.dagger:hilt-android:2.51.1")
  ksp("com.google.dagger:hilt-android-compiler:2.51.1")

  // Room
  implementation("androidx.room:room-runtime:2.6.1")
  ksp("androidx.room:room-compiler:2.6.1")

  // Kotlin Extensions and Coroutines support for Room
  implementation("androidx.room:room-ktx:2.6.1")

  // Permission library
  implementation("com.google.accompanist:accompanist-permissions:0.34.0")
}
