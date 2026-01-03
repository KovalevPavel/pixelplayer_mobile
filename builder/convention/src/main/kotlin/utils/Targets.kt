package utils

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.addTargets(withAndroid: Boolean) {
    if (withAndroid && !targets.asMap.keys.contains("android")) {
        androidTarget()
    }

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
