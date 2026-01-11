rootProject.name = "Pixelplayer"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("builder")
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

data class DepScope(
    val dir: String,
)

fun DepScope.addModule(module: String) {
    require(module.startsWith(":").not())
    include(":$module")
    project(":$module").projectDir = file("${rootProject.projectDir}/${this.dir}/$module")
}

fun withDirectory(dir: String, action: DepScope.() -> Unit) {
    DepScope(dir).apply(action)
}

include(":composeApp")
include(":core")
include(":core_design")
include(":core_ui")
include(":network")
include(":core_player")

withDirectory("login") {
    addModule("api_login")
    addModule("domain_login")
    addModule("feature_login")
}

withDirectory("storage") {
    addModule("api_storage")
    addModule("core_storage")
}

withDirectory("credentials") {
    addModule("api_credentials")
    addModule("core_credentials")
}

withDirectory("main_flow") {
    addModule("api_main_flow")
    addModule("core_main_flow")
    addModule("feature_main_flow")
}

withDirectory("artists") {
    addModule("api_artists")
    addModule("domain_artists")
    addModule("feature_artists")
}

withDirectory("albums") {
    addModule("api_albums")
    addModule("domain_albums")
    addModule("feature_albums")
}

withDirectory("tracks") {
    addModule("api_tracks")
    addModule("domain_tracks")
    addModule("feature_tracks")
}
