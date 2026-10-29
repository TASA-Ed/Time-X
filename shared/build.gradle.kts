import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilderFactory
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

@DisableCachingByDefault(because = "pyftsubset is an external tool whose version is managed by the developer")
abstract class SubsetFonts : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val fonts: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val localizedStrings: ConfigurableFileCollection

    @get:Input
    abstract val executable: Property<String>

    @get:OutputFile
    abstract val characterFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun subset() {
        val parser = DocumentBuilderFactory.newInstance().apply {
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        }.newDocumentBuilder()
        // Match Compose's string escapes after XML entities have been decoded.
        val escapes = Regex("""\\u([0-9a-fA-F]{4})|\\([nt\\])""")
        val text = localizedStrings.files.sortedBy { it.path }.joinToString("\n") { file ->
            escapes.replace(parser.parse(file).documentElement.textContent) { match ->
                if (match.groupValues[1].isNotEmpty()) {
                    match.groupValues[1].toInt(16).toChar().toString()
                } else {
                    when (match.groupValues[2]) {
                        "n" -> "\n"
                        "t" -> "\t"
                        else -> "\\"
                    }
                }
            }
        }
        val characters = characterFile.get().asFile
        characters.parentFile.mkdirs()
        characters.writeText(text + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", Charsets.UTF_8)

        val fontDirectory = outputDirectory.dir("font").get().asFile
        fontDirectory.mkdirs()
        fonts.files.sortedBy { it.name }.forEach { font ->
            execOperations.exec {
                commandLine(
                    this@SubsetFonts.executable.get(), font.absolutePath,
                    "--text-file=${characters.absolutePath}",
                    "--output-file=${fontDirectory.resolve(font.name).absolutePath}",
                    "--layout-features=*",
                )
            }
        }
    }
}

val subsetFonts = tasks.register<SubsetFonts>("subsetFonts") {
    group = "build"
    description = "Subsets Source Han Sans SC fonts using all localized resource text."
    fonts.from(
        "fontSources/source_han_sans_sc_bold.otf",
        "fontSources/source_han_sans_sc_regular.otf",
    )
    localizedStrings.from(fileTree("src/commonMain/composeResources") {
        include("values*/**/*.xml")
    })
    executable.convention(providers.gradleProperty("pyftsubsetExecutable").orElse("pyftsubset"))
    characterFile.set(layout.buildDirectory.file("fontSubset/characters.txt"))
    outputDirectory.set(layout.buildDirectory.dir("generated/fontSubset/composeResources"))
}

// A custom Compose resource directory replaces the default, so merge both inputs.
val prepareSubsetComposeResources = tasks.register<Sync>("prepareSubsetComposeResources") {
    from("src/commonMain/composeResources")
    from(subsetFonts.flatMap { it.outputDirectory })
    into(layout.buildDirectory.dir("generated/subsetComposeResources/commonMain"))
}

compose.resources {
    publicResClass = true
    customDirectory(
        sourceSetName = "commonMain",
        directoryProvider = layout.dir(prepareSubsetComposeResources.map { it.destinationDir }),
    )
}

kotlin {
    jvm()
    
    android {
       namespace = "top.tasaed.timex.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.kotlinx.coroutinesCore)
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}