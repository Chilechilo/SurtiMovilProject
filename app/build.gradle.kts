plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services") version "4.4.1" apply false
}

android {
    namespace = "com.surtiapp.surtimovil"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.surtiapp.surtimovil"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- Dependencias de Android y Kotlin ---
    implementation(libs.androidx.core.ktx)
    // Dejo KTX sin versión, ya que es la versión más limpia.
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Compose (Usando BOM) ---
    // BOM de Compose: Controla las versiones de todas las librerías 'libs.androidx.ui', 'libs.androidx.material3', etc.
    implementation(platform(libs.androidx.compose.bom))

    // Artefactos Compose (Sin versión explícita, controlados por el BOM)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // <- Esto usa el BOM
    implementation("androidx.compose.material:material-icons-extended") // Sin versión, el BOM lo maneja.

    // --- Firebase ---
    // Usando KTX para las versiones modernas
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    // --- Navegación ---
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- ViewModel (Versión recomendada para Compose) ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")


    // --- Networking y DataStore ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson para Retrofit (Consolidada en 2.9.0)
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- Otros ---
    implementation("androidx.compose.foundation:foundation") // Necesario para Pager, pero puede ser implícita. La dejo aquí.
    implementation("androidx.activity:activity-compose:1.9.2") // Ya incluida, pero la dejo si es necesario.
    implementation ("com.google.android.material:material:1.11.0") // Material 1 para vistas tradicionales
    implementation("com.google.code.gson:gson:2.10.1") // Gson standalone

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
