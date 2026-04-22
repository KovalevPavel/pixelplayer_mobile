import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.artifact.SingleArtifact
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.android
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.register
import java.io.File

val appVersionCode = 1
val appVersionName = "1.0.0"

fun buildArtifactName(
    versionName: String,
    versionCode: Int,
    buildVariant: String,
): String {
    return "Pixelplayer_${versionName}(${versionCode})_${buildVariant}"
}

abstract class RenameApkArtifactsTask : DefaultTask() {
    @get:InputFiles
    abstract val inputApkFolder: DirectoryProperty

    @get:OutputDirectory
    abstract val outputApkFolder: DirectoryProperty

    @get:Input
    abstract val artifactName: Property<String>

    @get:Internal
    abstract val transformationRequest: Property<ArtifactTransformationRequest<RenameApkArtifactsTask>>

    @TaskAction
    fun renameApks() {
        transformationRequest.get().submit(this) { builtArtifact ->
            val inputFile = File(builtArtifact.outputFile)
            val outputFile = outputApkFolder.file("${artifactName.get()}.apk").get().asFile
            outputFile.parentFile.mkdirs()
            inputFile.copyTo(outputFile, overwrite = true)
            outputFile
        }
    }
}

abstract class RenameBundleTask : DefaultTask() {
    @get:InputFile
    abstract val inputBundle: RegularFileProperty

    @get:OutputFile
    abstract val outputBundle: RegularFileProperty

    @TaskAction
    fun renameBundle() {
        val inputFile = inputBundle.get().asFile
        val outputFile = outputBundle.get().asFile
        outputFile.parentFile.mkdirs()
        inputFile.copyTo(outputFile, overwrite = true)
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    android {
        namespace = "kovp.pixelplayer"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        flavorDimensions.add("default")

        defaultConfig {
            applicationId = "kovp.pixelplayer"
            minSdk = libs.versions.android.minSdk.get().toInt()
            targetSdk = libs.versions.android.targetSdk.get().toInt()
            versionCode = appVersionCode
            versionName = appVersionName
        }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
        buildTypes {
            getByName("debug") {
                applicationIdSuffix = ".debug"
            }

            getByName("release") {
                isMinifyEnabled = true
                isShrinkResources = true
            }
        }

        buildFeatures {
            buildConfig = true
        }

        productFlavors {
            register("default") {
                isDefault = true
            }

            register("demo") {
                applicationIdSuffix = ".demo"
            }
        }
    }
}

androidComponents {
    onVariants(selector().all()) { variant ->
        val artifactFileName = buildArtifactName(
            versionName = appVersionName,
            versionCode = appVersionCode,
            buildVariant = variant.name,
        )
        val variantTaskName = variant.name.replaceFirstChar(Char::titlecase)
        val renameApkTask = tasks.register<RenameApkArtifactsTask>(
            "rename${variantTaskName}ApkArtifacts"
        ) {
            artifactName.set(artifactFileName)
        }
        val apkTransformationRequest = variant.artifacts
            .use(renameApkTask)
            .wiredWithDirectories(
                RenameApkArtifactsTask::inputApkFolder,
                RenameApkArtifactsTask::outputApkFolder,
            )
            .toTransformMany(SingleArtifact.APK)

        renameApkTask.configure {
            transformationRequest.set(apkTransformationRequest)
        }

        val renameBundleTask = tasks.register<RenameBundleTask>(
            "rename${variantTaskName}BundleArtifact"
        )
        variant.artifacts
            .use(renameBundleTask)
            .wiredWithFiles(
                RenameBundleTask::inputBundle,
                RenameBundleTask::outputBundle,
            )
            .withName("$artifactFileName.aab")
            .toTransform(SingleArtifact.BUNDLE)
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(project(":core"))
    implementation(project(":core_design"))
    implementation(project(":core_player"))
    implementation(project(":network"))

    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    implementation(libs.google.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.koin.compose)
}
