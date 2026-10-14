import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Módulo Kotlin puro: as regras de negócio não conhecem o Android
plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    // api: o Flow faz parte das interfaces públicas (ex.: SunscreenRepository)
    api(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
