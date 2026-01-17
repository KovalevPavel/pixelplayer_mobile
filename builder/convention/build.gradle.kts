plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.kotlin.multiplatform.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.multiplatform.gradlePlugin)

    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    compileOnly(libs.androidx.benchmark.baseline.profile.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("kotlinPlugin") {
            id = libs.plugins.pixelplayer.kotlin.get().pluginId
            implementationClass = "plugins.KotlinPlugin"
        }
    }

    plugins {
        create("libraryPlugin") {
            id = libs.plugins.pixelplayer.library.get().pluginId
            implementationClass = "plugins.LibraryPlugin"
        }
    }

    plugins {
        create("composePlugin") {
            id = libs.plugins.pixelplayer.compose.get().pluginId
            implementationClass = "plugins.ComposePlugin"
        }
    }

    plugins {
        create("apiPlugin") {
            id = libs.plugins.pixelplayer.api.get().pluginId
            implementationClass = "plugins.ApiPlugin"
        }
    }
}
