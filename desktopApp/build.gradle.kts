import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.compose.material3)
    implementation(libs.compose.components.resources)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "top.tasaed.timex.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            modules("jdk.unsupported")
            packageName = "Time X"
            packageVersion = rootProject.version.toString()
            description = "Check accurate time"
            vendor = "TASA-Ed"

            windows {
                iconFile.set(project.file("src/main/resources/icon.ico"))
                menuGroup = "TASA-Ed Apps"
                shortcut = true
            }
            linux {
                iconFile.set(project.file("../shared/src/commonMain/composeResources/drawable/icon.png"))
            }

            buildTypes.release.proguard {
                isEnabled.set(true)
                configurationFiles.from(project.file("compose-desktop.pro"))
            }
        }
    }
}
