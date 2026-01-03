package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.addTargets
import utils.libs

@Suppress("unused")
class KotlinPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply {
            project.libs.plugins.kotlinMultiplatform.get().pluginId.let(::apply)
        }

        project.extensions.configure<KotlinMultiplatformExtension> {
            jvm()
            jvmToolchain {
                this.languageVersion.set(
                    project.libs.versions.java.sdk.get().let(JavaLanguageVersion::of),
                )
            }

            addTargets(withAndroid = false)

            sourceSets.getByName("commonMain")
                .dependencies {
                    implementation(project.libs.kotlinx.coroutines.get())
                    implementation(project.libs.kotlinx.immutable.get())
                }
        }
    }
}
