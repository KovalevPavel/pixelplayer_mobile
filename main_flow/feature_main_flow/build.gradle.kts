plugins {
    alias(libs.plugins.pixelplayer.library)
    alias(libs.plugins.pixelplayer.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain_main_flow"))
            implementation(project(":api_storage"))
        }
    }
}
