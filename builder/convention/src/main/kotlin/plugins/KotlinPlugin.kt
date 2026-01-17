package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.configureKotlin
import utils.libs

@Suppress("unused")
class KotlinPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply {
            project.libs.plugins.kotlin.multiplatform.core.get().pluginId.let(::apply)
        }

        project.configureKotlin(kotlinOnly = true)
    }
}
