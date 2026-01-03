package plugins

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.libs

@Suppress("unused")
class ApiPlugin : ComposePlugin() {
    override fun apply(project: Project) {
        project.pluginManager.apply {
            listOf(
                project.libs.plugins.pixelplayer.library,
                project.libs.plugins.pixelplayer.compose,
            )
                .forEach { apply(it.get().pluginId) }
        }

        project.extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain")
                .dependencies {
                    implementation(project.libs.navigation.get())
                }
        }
    }
}
