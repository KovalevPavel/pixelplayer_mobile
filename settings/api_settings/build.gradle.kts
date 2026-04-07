plugins {
    alias(libs.plugins.pixelplayer.api)
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(project(":core"))
        implementation(project(":api_storage"))
        implementation(project(":core_player"))
        implementation(project(":api_credentials"))
        implementation(project(":feature_settings"))
    }
}
