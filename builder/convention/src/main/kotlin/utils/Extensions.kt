package utils

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

fun Project.configureKotlin(kotlinOnly: Boolean) {
    pluginManager.apply {
        libs.plugins.kotlin.multiplatform.core.get().pluginId.let(::apply)
    }

    extensions.configure<KotlinMultiplatformExtension> {
        if (kotlinOnly) {
            jvm()
        }
        addNativeTargets()

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

private fun KotlinMultiplatformExtension.addNativeTargets() {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    )
        .filterNot { it in targets }
        .forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = project.name
                isStatic = true
            }
        }
}
