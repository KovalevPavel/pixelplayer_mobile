package utils

import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@OptIn(ExperimentalKotlinGradlePluginApi::class)
fun Project.configureKotlin(withAndroid: Boolean) {
    pluginManager.apply {
        libs.plugins.kotlin.multiplatform.get().pluginId.let(::apply)
    }

    extensions.configure<KotlinMultiplatformExtension> {
        addTargets(withAndroid = withAndroid)
        jvmToolchain {
            this.languageVersion.set(
                libs.versions.java.sdk.get().let(JavaLanguageVersion::of),
            )
        }

        sourceSets.getByName("commonMain")
            .dependencies {
                implementation(libs.kotlinx.coroutines.get())
                implementation(libs.kotlinx.immutable.get())
            }
    }
}
