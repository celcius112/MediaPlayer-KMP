import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    id("com.vanniktech.maven.publish") version "0.29.0"
    id("com.google.osdetector") version "1.7.3"
}

kotlin {
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    js {
        browser()
        binaries.executable()
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.material3)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.materialIconsExtended)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.startup.runtime)
                implementation(libs.core)
                implementation(libs.custom.ui)
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.activityCompose)
                implementation(libs.compose.uitooling)
                implementation(compose.ui)
                implementation("androidx.media3:media3-exoplayer:1.4.0")
                implementation("androidx.media3:media3-exoplayer-dash:1.4.0")
                implementation("androidx.media3:media3-ui:1.4.0")
                implementation("androidx.media3:media3-session:1.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
                implementation(libs.kotlinx.coroutines.android)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                val fxSuffix = when (osdetector.classifier) {
                    "linux-x86_64" -> "linux"
                    "linux-aarch_64" -> "linux-aarch64"
                    "windows-x86_64" -> "win"
                    "osx-x86_64" -> "mac"
                    "osx-aarch_64" -> "mac-aarch64"
                    else -> throw IllegalStateException("Unknown OS: ${osdetector.classifier}")
                }
                implementation("org.openjfx:javafx-base:19:${fxSuffix}")
                implementation("org.openjfx:javafx-graphics:19:${fxSuffix}")
                implementation("org.openjfx:javafx-controls:19:${fxSuffix}")
                implementation("org.openjfx:javafx-swing:19:${fxSuffix}")
                implementation("org.openjfx:javafx-web:19:${fxSuffix}")
                implementation("org.openjfx:javafx-media:19:${fxSuffix}")
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(compose.html.core)
            }
        }
    }
}

android {
    namespace = "org.jetbrains.kotlinx.multiplatform.library.template"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = 21
    }
}
mavenPublishing {
    coordinates(
        groupId = "io.github.khubaibkhan4",
        artifactId = "mediaplayer-kmp",
        version = "1.1.0"
    )

    pom {
        name.set("MediaPlayer-KMP")
        description.set("Compose & Kotlin Multiplatform Library that Help You to Play Videos/ YouTube Videos Native Notifications on Android, iOS, Web & Desktop.")
        inceptionYear.set("2024")
        url.set("https://github.com/KhubaibKhan4/MediaPlayer-KMP")

        licenses {
            license {
                name.set("GPL-2.0")
                url.set("https://opensource.org/license/gpl-2-0")
            }
        }

        developers {
            developer {
                id.set("khubaibkhan4")
                name.set("Muhammad Khubaib Imtiaz")
                email.set("18.bscs.803@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/KhubaibKhan4/MediaPlayer-KMP")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}

task("testClasses") {}
tasks.withType<JavaExec> {
    jvmArgs = listOf("--add-modules", "javafx.controls,javafx.fxml", "--add-opens", "javafx.graphics/javafx.scene=ALL-UNNAMED", "--add-opens", "javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED", "--add-opens", "javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED")
}
