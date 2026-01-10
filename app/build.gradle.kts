import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.detekt)
}

val jvmVersion = libs.versions.jvm.get().toInt()
val androidSDKTarget = libs.versions.androidSDKTarget.get().toInt()
val androidSDKMinimum = libs.versions.androidSDKMinimum.get().toInt()

android {
    namespace = "me.mikucat.clementine.app"
    compileSdk {
        version = release(androidSDKTarget)
    }
    defaultConfig {
        applicationId = "me.mikucat.clementine.app"
        minSdk = androidSDKMinimum
        targetSdk = androidSDKTarget
        versionCode = 1
        versionName = "0.1.0"
    }
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "NON_EXISTS")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = System.getenv("KEY_ALIAS") ?: ""
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(jvmVersion)
        targetCompatibility = JavaVersion.toVersion(jvmVersion)
    }
    buildFeatures {
        compose = true
    }
}

detekt {
    buildUponDefaultConfig = true
    source.setFrom("src/main/kotlin")
    config.setFrom("$projectDir/config/detekt.yaml")
    ignoredBuildTypes = listOf("release")
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = jvmVersion.toString()
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = jvmVersion.toString()
}

dependencies {
    implementation(project(":lib"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.bundles.nav3)

    implementation(libs.datastore)

    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.http)

    implementation(libs.bundles.camerax)

    implementation(libs.accompanist.permissions)

    implementation(libs.zxing)

    implementation(libs.material)

    detektPlugins(libs.compose.rules.detekt)
}
