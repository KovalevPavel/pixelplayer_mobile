package plugins

import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.configureKotlin
import utils.libs

@Suppress("unused")
class LibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val hasLibrary = project.pluginManager.hasPlugin(
            project.libs.plugins.pixelplayer.library.get().pluginId,
        )
        project.pluginManager.apply {
            project.libs.plugins.kotlin.multiplatform.library.get().pluginId.let(::apply)
            project.libs.plugins.kotlinx.serialization.get().pluginId.let(::apply)
        }

        project.configureKotlin(kotlinOnly = false)

        project.extensions.configure<KotlinMultiplatformAndroidComponentsExtension> {
            finalizeDsl { androidDsl ->
                androidDsl.namespace = "kovp.pixelplayer.${project.name}"
                androidDsl.compileSdk = project.libs.versions.android.compileSdk.get().toInt()
                androidDsl.minSdk = project.libs.versions.android.minSdk.get().toInt()
            }
        }

        project.extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain")
                .dependencies {
                    listOf(
                        project.libs.androidx.lifecycle.viewmodelCompose,
                        project.libs.koin.compose,
                        project.libs.koin.compose.viewmodel,
                    )
                        .forEach { implementation(it) }

                    if (project.name != "core" && hasLibrary) {
                        implementation(project(":core"))
                    }
                }
        }
    }
}
