import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.detekt)
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.ktor.bom))
    implementation(libs.bundles.ktor)
    api(platform(libs.ktor.bom))
    api(libs.ktor.http)
    api(libs.ktor.client.logging)
}

detekt {
    buildUponDefaultConfig = true
    source.setFrom("src/main/kotlin")
    config.setFrom("$projectDir/config/detekt.yaml")
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "21"
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "21"
}
