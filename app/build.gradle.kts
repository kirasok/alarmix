import java.util.Properties
import java.io.FileInputStream

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.devtools.ksp")
  id("com.google.dagger.hilt.android")
}

// Create a variable called keystorePropertiesFile, and initialize it to your
// keystore.properties file, in the rootProject folder.
val keystorePropertiesFile = rootProject.file("key.properties")

// Initialize a new Properties() object called keystoreProperties.
val keystoreProperties = Properties()

// Load your keystore.properties file into the keystoreProperties object.
keystoreProperties.load(FileInputStream(keystorePropertiesFile))


android {
  namespace = "io.github.kirasok.alarmix"
  compileSdk = 34

  defaultConfig {
    applicationId = "io.github.kirasok.alarmix"
    minSdk = 31
    targetSdk = 34
    versionCode = 2
    versionName = "0.1.1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  signingConfigs {
    create("release") {
      keyAlias = keystoreProperties["keyAlias"] as String
      keyPassword = keystoreProperties["keyPassword"] as String
      storeFile = file(keystoreProperties["storeFile"] as String)
      storePassword = keystoreProperties["storePassword"] as String
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
    kotlinCompilerExtensionVersion = "1.5.10"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.12.0")
  implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.23"))
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
  implementation("androidx.activity:activity-compose:1.8.2")
  implementation(platform("androidx.compose:compose-bom:2024.03.00"))
  implementation("androidx.compose.ui:ui:1.6.4")
  implementation("androidx.compose.ui:ui-graphics:1.6.4")
  implementation("androidx.compose.ui:ui-tooling-preview:1.6.4")
  implementation("androidx.compose.material3:material3:1.2.1")
  implementation("androidx.room:room-common:2.6.1")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation(platform("androidx.compose:compose-bom:2024.03.00"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.4")
  debugImplementation("androidx.compose.ui:ui-tooling:1.6.4")
  debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.4")

  // Compose dependencies
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
  implementation("androidx.navigation:navigation-compose:2.7.7")
  implementation("androidx.compose.material:material-icons-extended:1.6.4")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

  // Coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

  //Dagger - Hilt
  implementation("com.google.dagger:hilt-android:2.51")
  ksp("com.google.dagger:hilt-android-compiler:2.51")

  // Room
  implementation("androidx.room:room-runtime:2.6.1")
  ksp("androidx.room:room-compiler:2.6.1")

  // Kotlin Extensions and Coroutines support for Room
  implementation("androidx.room:room-ktx:2.6.1")

  // Permission library
  implementation("com.google.accompanist:accompanist-permissions:0.32.0")
}