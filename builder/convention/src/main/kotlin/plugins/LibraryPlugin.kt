package plugins

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.configureKotlin
import utils.libs

@Suppress("unused")
class LibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply {
            project.libs.plugins.androidLibrary.get().pluginId.let(::apply)
            project.libs.plugins.kotlinx.serialization.get().pluginId.let(::apply)
        }

        project.configureKotlin(withAndroid = true)

        project.extensions.configure<LibraryExtension> {
            namespace = "kovp.pixelplayer.${project.name}"
            compileSdk = project.libs.versions.android.compileSdk.get().toInt()
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
                }
        }
    }
}
