plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
    id("org.jetbrains.compose") version "1.6.10"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.androidx.room.common.jvm)
    implementation(compose.desktop.currentOs)
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")
    implementation("org.jetbrains.exposed:exposed-core:0.50.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.50.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.50.1")
    implementation(compose.materialIconsExtended)
    implementation(libs.androidx.benchmark.traceprocessor.jvm)
}

compose.desktop {
    application {
        mainClass = "com.example.desktopapp.MainKt"

        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe)
            packageName = "ComputerManager"
            packageVersion = "1.0.0"
        }
    }
}