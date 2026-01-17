package plugins

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.libs

@Suppress("unused")
class ComposePlugin : ComposePlugin() {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun apply(project: Project) {
        val hasCompose = project.pluginManager.hasPlugin(
            project.libs.plugins.pixelplayer.compose.get().pluginId
        )

        val composeDeps = Dependencies(project)
        project.pluginManager.apply {
            listOf(
                project.libs.plugins.compose.multiplatform,
                project.libs.plugins.compose.compiler,
            )
                .map { it.get().pluginId }
                .forEach(::apply)
        }

        project.extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain")
                .dependencies {
                    implementation(project.libs.jetbrains.compose.runtime)
                    implementation(project.libs.jetbrains.compose.foundation.foundation)
                    implementation(project.libs.jetbrains.compose.material.material)
                    implementation(project.libs.jetbrains.compose.material.icons)
                    implementation(project.libs.jetbrains.compose.ui.core)
                    implementation(project.libs.jetbrains.compose.components.resources)
                    implementation(project.libs.jetbrains.compose.ui.toolingPreview)
                    implementation(project.libs.navigation)
                    implementation(project.libs.jetbrains.compose.backhandler)

                    if (project.name != "core_design" && hasCompose) {
                        implementation(project(":core_design"))

                        if (project.name != "core_ui") {
                            implementation(project(":core_ui"))
                        }
                    }

                    listOf(
                        project.libs.androidx.lifecycle.viewmodelCompose,
                        project.libs.androidx.lifecycle.runtimeCompose,
                        project.libs.coil.core,
                        project.libs.coil.network,
                    )
                        .forEach { implementation(it.get()) }
                }
        }

        project.dependencies {
            add("androidRuntimeClasspath", project.libs.jetbrains.compose.ui.tooling)
        }
    }
}
